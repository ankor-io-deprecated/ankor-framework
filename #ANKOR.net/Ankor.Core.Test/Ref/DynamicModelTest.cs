﻿using System.Collections.Specialized;
﻿using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
﻿using Ankor.Core.Change;
﻿using Ankor.Core.Event;
﻿using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.Ref {

	[TestFixture]
	public class DynamicModelTest {
		private IInternalModel jModel;
		private DynaModel dModel;

		private dynamic newModelValues;

		[SetUp]
		public void SetUp() {
			jModel = new RModel();
			var initialModel = JsonMessageMapper.ParseSnippet(
			@"{
				'n1'	: {
							'n2' : { 
										'n3' : {
													'username'	: 'david',
													'age'				: 14
													}
										}
							},
				'v1' : 'schlumpf'								
			}");

			jModel.UpdateFromExternal("root", initialModel);

			dModel = new DynaModel(jModel);

			newModelValues = TestUtils.ParseSnippet(jModel,
			@"{
				'n1'	: {
							'n2' : { 
										'n3' : {
													'username'	: 'david2',
													'age'				: 16
													}
										}
							},
				'v1' : 'schlumpf2'								
			}");

		}

		[Test]
		public void ReadValueStatic() {
			Assert.AreEqual("schlumpf", dModel.RootRef.AppendPath("v1").Value);
			Assert.AreEqual("david", dModel.RootRef.AppendPath("n1.n2.n3.username").Value);
			Assert.AreEqual(14L, dModel.RootRef.AppendPath("n1.n2.n3.age").Value);
		}

		[Test]
		public void SaveSubRefStatic() {
			DynaRef n2 = dModel.RootRef.AppendPath("n1.n2");
			Assert.AreEqual("david", n2.AppendPath("n3.username").Value);
		}

		[Test]
		public void ChangeValueStatic() {
			DynaRef v1Ref = dModel.RootRef.AppendPath("v1");
			Assert.AreEqual("schlumpf", v1Ref.Value);
			v1Ref.Value = "hias";
			Assert.AreEqual("hias", v1Ref.Value);
		}

		[Test]
		public void ChangeRootValue() {
			dModel.Root.Value = 4;
			jModel.PrintModelToDebug();

			Assert.AreEqual(4, dModel.Root.Value);
		}

		[Test]
		public void ReadValueDyn() {

			Assert.AreEqual("schlumpf", dModel.Root.v1.Value);
			Assert.AreEqual("david", dModel.Root.n1.n2.n3.username.Value);
			Assert.AreEqual("david", dModel.Root.n1.n2.n3.username.Value);
			Assert.AreEqual(14L, dModel.Root.n1.n2.n3.age.Value);

		}

		[Test]
		public void SaveSubRefDyn() {
			dynamic n2 = dModel.Root.n1.n2;
			Assert.AreEqual("david", n2.n3.username.Value);
		}

		[Test]
		public void ChangeValueDyn() {
			dynamic n3 = dModel.Root.n1.n2.n3;
			Assert.AreEqual(14L, n3.age.Value);
			n3.age.Value = 32L;
			Assert.AreEqual(32L, n3.age.Value);
		}

		[Test]
		public void SetValueDyn() {
			dModel.Root.v1 = "super";
			Assert.AreEqual("super", dModel.Root.v1.Value);

		}

		[Test]
		public void SetDynaRefToDynaRefShouldExecuteValue() {
			dModel.Root.v1 = dModel.Root.n1.n2.n3.username;

			Assert.AreEqual("david", dModel.Root.v1.Value);

			dModel.RootRef.AppendPath("v1").Value = dModel.Root.n1.n2.n3.age;

			Assert.AreEqual(14L, dModel.Root.v1.Value);
			
		}

		// TODO also work iwth setting w.o. Value? n3.age = 15L

		[Test]
		public void UseRefsAfterJsonModelReplace() {
			dynamic n2 = dModel.Root.n1.n2;
			Assert.AreEqual("david", n2.n3.username.Value);

			jModel.UpdateFromExternal("root", newModelValues);

			Assert.AreEqual("david2", dModel.RootRef.AppendPath("n1.n2.n3.username").Value);

			Assert.AreEqual("schlumpf2", dModel.Root.v1.Value);
			Assert.AreEqual("david2", dModel.Root.n1.n2.n3.username.Value);

			Assert.AreEqual("david2", n2.n3.username.Value);
		}

		[Test]
		public void ChangeNotificationOnDirectPropertyWorks() {

			// this can/should work just on "Value"

			DynaRef v1Ref = dModel.RootRef.AppendPath("v1");
			string changedPropName = null;
			v1Ref.PropertyChanged += (sender, args) => changedPropName = args.PropertyName;
			v1Ref.Value = "erwin";
			Assert.AreEqual("Value", changedPropName);


			dynamic uRef = dModel.Root.n1.n2.n3.username;
			changedPropName = null;
			((INotifyPropertyChanged)uRef).PropertyChanged += (sender, e) => changedPropName = e.PropertyName;
			uRef.Value = "erwin";
			Assert.AreEqual("Value", changedPropName);

		}

		// TODO How to handle this events? now property changed fires in both directions
		// Maybe cleaner: The "PropertyChanged" is just for data binding, therefor we want NO notification in this case
		// generic change event has to be used to get notified pattern wise?
		[Test]
		public void ChangeNotificationOnRootWorks() {

			DynaRef v1Ref = dModel.RootRef.AppendPath("v1");
			string changedPropName = null;
			dModel.RootRef.PropertyChanged += (sender, args) => changedPropName = args.PropertyName;
			v1Ref.Value = "erwin";
			Assert.AreEqual("v1", changedPropName);

		}

		[Test]
		public void ChangeNotificationOnDeepPathWorks() {

			dynamic uRef = dModel.Root.n1.n2.n3;
			string changedPropName = null;
			((INotifyPropertyChanged)uRef).PropertyChanged += (sender, e) => changedPropName = e.PropertyName;
			uRef.username.Value = "erwin";
			Assert.AreEqual("username", changedPropName);
		}

		[Test]
		public void ChangeNotificationsWithPathWorks() {
			string changedPropName = null;
			((INotifyPropertyChanged)dModel.Root.n1.n2).PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			dModel.Root.n1.n2.n3.username.Value = "erwin";
			Assert.AreEqual("n3.username", changedPropName);

		}

		[Test]
		public void FireNoChangeNotificationForResetSameValue() {
			DynaRef v1Ref = dModel.RootRef.AppendPath("v1");
			string changedPropName = null;
			v1Ref.PropertyChanged += (sender, args) => changedPropName = args.PropertyName;
			v1Ref.Value = "schlumpf";
			Assert.AreEqual(null, changedPropName);
		}

		[Test]
		public void FireNoChangeNotificationForDifferentPath() {
			string changedPropName = null;
			((INotifyPropertyChanged)dModel.Root.n1.n2.n3).PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			dModel.RootRef.AppendPath("v1").Value = "somethingnew";
			Assert.AreEqual(null, changedPropName, "expect no change");

		}

		[Test]
		public void ChangeNotificationAfterModelReplaceWorks() {
			string changedPropName = null;
			((INotifyPropertyChanged)dModel.Root.n1.n2.n3).PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			dModel.Root.n1.n2.n3.username.Value = "dagobert";
			Assert.AreEqual("username", changedPropName);
			changedPropName = null;

			jModel.UpdateFromExternal("root", newModelValues);

			dModel.Root.n1.n2.n3.username.Value = "friesenbichler";
			Assert.AreEqual("username", changedPropName);
		}

		[Test]
		public void ChangeNotificationBetweenRefsWorks() {
			DynaRef ref1 = dModel.RootRef.AppendPath("v1");
			string changedPropName = null;
			dModel.RootRef.PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			DynaRef ref2 = dModel.RootRef.AppendPath("v1");

			ref2.Value = "erwin";
			Assert.AreEqual("v1", changedPropName);

			Assert.AreEqual("erwin", ref1.Value);
			Assert.AreEqual("erwin", ref2.Value);
		}

		[Test]
		public void ChangeNotificationBetweenDynamicRefsWorks() {
			dynamic ref1 = dModel.Root.n1.n2.n3;
			string changedPropName = null;
			((INotifyPropertyChanged)ref1).PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			dynamic ref2 = dModel.Root.n1.n2.n3;
			ref2.username = "erwin";

			Assert.AreEqual("username", changedPropName);

			Assert.AreEqual("erwin", ref1.username.Value);
			Assert.AreEqual("erwin", ref2.username.Value);
		}

		[Test]
		public void RemoteChangeOnRootTriggersValueNotification() { // is this fine behavior?

			bool changeForRemote = false;
			jModel.EventRegistry.Add(new ChangeEventListener(e => {
				if (e.Source is LocalSource) {
					changeForRemote = true;
				}
			}));

			
			//jModel.ChangeForRemote += (path, change) => changeForRemote = true;

			string changedPropName = null;
			dModel.RootRef.PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			jModel.UpdateFromExternal("root", newModelValues); // from remote

			changeForRemote.Should().Be(false);
			changedPropName.Should().Be("Value");
		}

		[Test]
		public void RemoteChangeTriggersValueNotification() {

			string changedPropName = null;
			dModel.RootRef.AppendPath("v1").PropertyChanged += (sender, args) => changedPropName = args.PropertyName;

			jModel.UpdateFromExternal("root", newModelValues); // from remote

			changedPropName.Should().Be("Value");
		}

		[Test]
		public void InternalModelChangeShouldTriggerRemote() {

			ChangeEvent ce = null;

			jModel.EventRegistry.Add(new ChangeEventListener(e => {
				if (e.Source is LocalSource) {
					// relay to remote in reality
					ce = e;
				}
			}));

			dModel.Root.n1.n2.n3.age.Value = 14; // from internal

			ce.Property.Path.Should().Be("root.n1.n2.n3.age");
			ce.Change.Value.Should().Be(14);
		}


		// TODO test somewhere not on a leaf: map (== dynamic) oder array/list

	}


	// THERE seem to be 2 options: create a ref for every navigation step, or create just on .Ref 
	// the ref always just delegates to the refContext == jModel in both directions. 
	// listen to a ref means listen to the refContext in reality
}
