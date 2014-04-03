using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ankor.Core.Action;
using Ankor.Core.Event;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.Ref {
	[TestFixture]
	public class ActionEventsTest {
		
		private DynaModel dModel;
		private IInternalModel jModel;


		[SetUp]
		public void SetUp() {
			jModel = new RModel();

			var initialModel = TestUtils.ParseSnippet(jModel, @"{
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

		}

		[Test]
		public void ReceiveLocallyFiredAction() {
			var n2Ref = dModel.RootRef.AppendPath("n1.n2");
			AAction receivedAction = null;
			n2Ref.ActionReceived += (sender, e) => receivedAction = e.Action;

			var wrongRef = dModel.RootRef.AppendPath("n1");
			bool receivedAtWrongRef = false;
			wrongRef.ActionReceived += (sender, e) => receivedAtWrongRef = true;

			ActionEvent remoteReceivedActionEvent = null;
			jModel.EventRegistry.Add(new ActionEventListener(e => remoteReceivedActionEvent = e));
			
			//ActionForRemote += (path, action) => { remoteReceivedPath = path; action.Name.Should().Be("funnyAction"); };


			dModel.RootRef.AppendPath("n1.n2").Fire(new AAction("funnyAction"));


			receivedAction.Should().NotBeNull();
			receivedAction.Name.Should().Be("funnyAction");

			receivedAtWrongRef.Should().BeFalse();

			remoteReceivedActionEvent.Action.Name.Should().Be("funnyAction");
			remoteReceivedActionEvent.Property.Path.Should().Be("root.n1.n2");
			
		}

		// TODO add test for REMOVE

		[Test]
		public void ReceiveRemoteFiredAction() {
			var n2Ref = dModel.RootRef.AppendPath("n1.n2");
			AAction receivedAction = null;
			n2Ref.ActionReceived += (sender, e) => receivedAction = e.Action;

			bool remoteReceivedAction = false;
			//jModel.ActionForRemote += (path, action) => { remoteReceivedAction = true; };
			jModel.EventRegistry.Add(new ActionEventListener(e => {
				if (e.Source is RemoteSource) {
					remoteReceivedAction = true;
				}
			}));

			// fire FROM remote source
			((DynaRef)dModel.RootRef.AppendPath("n1.n2")).Fire(new AAction("funnyAction"), new RemoteSource());

//			jModel.FireActionFromRemote("root.n1.n2", new AAction("funnyAction"));

			remoteReceivedAction.Should().BeFalse();
			receivedAction.Should().NotBeNull();
			receivedAction.Name.Should().Be("funnyAction");
			
		}

		// TODO remote notify test

	}

	
}
