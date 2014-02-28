using System;
using System.Collections.Generic;
using Ankor.Core.Change;
using Ankor.Core.Event;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;

namespace Ankor.Core.Test.Ref {

	[TestFixture]
	public class InternalModelTest {
		private IInternalModel model;

		[SetUp]
		public void SetUp() {

			model = new RModel();
			//model = new JsonModel();
			var newValues = Parse(
			@"{
				'username'	: 'david',
				'age'				:	32,
				'u2'				: {
											'username'	:	'herbert',
											'age'				: 14
											},
				'list'			: [1,2,3,4]
			}");
			model.UpdateFromExternal("", newValues);	
		}

		private object Parse(string json) {
			return TestUtils.ParseSnippet(model, json);
		}


		[Test]
		public void TestModelUpdate() {


			Assert.AreEqual("david", model.Root.username.Value);
			Assert.AreEqual(32L, model.Root.age.Value);

			model.UpdateFromExternal("age", 33);

			Assert.AreEqual("david", model.Root.username.Value);
			Assert.AreEqual(33L, model.Root.age.Value);

			model.UpdateFromExternal("u2.username", "schlumpf");
			Assert.AreEqual("schlumpf", model.Root.u2.username.Value);
		}

		[Test]
		public void TestReplaceModelUpdate() {

			var newValues = Parse(@"{
				'username'	: 'nobsi',
			}");
			model.UpdateFromExternal("", newValues);
			model.PrintModelToDebug();


			Assert.AreEqual("nobsi", model.Root.username.Value);
			var age = model.Root.age;
			Assert.AreEqual(null, age.Value);
		}

		[Test]
		public void ReplaceModelPartUpdate() {
			var newValues = Parse(@"{
				'username'	: 'david2',
				'age'				:	322
			}");

			model.UpdateFromExternal("u2", newValues);			

			Assert.AreEqual("david", model.Root.username.Value);
			Assert.AreEqual("david2", model.Root.u2.username.Value);
			Assert.AreEqual(322L, model.Root.u2.age.Value);

		}

		[Test]
		public void TestNewPathUpdate() {

			var newValues = Parse(@"{
				'username'	: 'david2',
				'age'				:	22
			}");

			model.UpdateFromExternal("user2", newValues);
			model.PrintModelToDebug();

			Assert.AreEqual("david", model.Root.username.Value);
			Assert.AreEqual("david2", model.Root.user2.username.Value);
			Assert.AreEqual(22L, model.Root.user2.age.Value);
		}

		// We dont want this to work most probably
		[Test]
		public void TestNewDeepPathUpdate() {
			var newValues = Parse(@"{
				val : 4
			}");
			model.UpdateFromExternal("n1.n2.n3", newValues);

			model.PrintModelToDebug();

			Assert.AreEqual(4L, model.Root.n1.n2.n3.val.Value);

			// TODO test deep path on empty root
			
		}

		[Test]
		public void UpdateViaDynamic() {

			model.Root.age = 34;

			Assert.AreEqual(34L, model.Root.age.Value);
		}
		
		[Test]
		public void TestModelChangedFromRemote() {

			var newValues = Parse(@"{
				'username'	: 'david',
				'age'				:	32
			}");

			bool changeForRemote = false;

			model.EventRegistry.Add(new ChangeEventListener(e => {
				if (e.Source is LocalSource) {
					changeForRemote = true;
				}
			}));

			
//			model.ChangeForRemote += (path, change) => changeForRemote = true;
			
			model.UpdateFromExternal("", newValues); // from remote

			changeForRemote.Should().Be(false);
			
		}

		[Test]
		public void UpdateArrayFromRemote() {

			dynamic newValues = Parse(@"{			
				'sport'   : []
			}");

			model.UpdateFromExternal("hobbies", newValues.sport); // just add the array, not the whole snippet
			model.PrintModelToDebug();

			//Assert.AreEqual("bike", model.Root.hobbies[1].Value);

			newValues = Parse(@"{			
				'sport'   : ['drink', 'eat', 'sleep']
			}");
			model.UpdateFromExternal("hobbies", newValues.sport);  // just add the array, not the whole snippet
			model.PrintModelToDebug();

			Assert.AreEqual("eat", model.Root.hobbies[1].Value);
		}

		[Test]
		public void InsertChangeFromRemote() {
			Assert.AreEqual(4, model.Root.list.Value.Count);

			model.UpdateFromExternal("list", Change.Change.InsertChange(4, 44));
			Assert.AreEqual(5, model.Root.list.Value.Count);
			Assert.AreEqual(44, model.Root.list[4].Value);

			model.UpdateFromExternal("list", Change.Change.InsertChange(0, 44));
			Assert.AreEqual(44, model.Root.list[0].Value);
			Assert.AreEqual(6, model.Root.list.Value.Count);
			
		}

		[Test]
		public void DeleteChangeFromRemote() {
			Assert.AreEqual(4, model.Root.list.Value.Count);

			model.UpdateFromExternal("list", Change.Change.DeleteChange(0));
			Assert.AreEqual(3, model.Root.list.Value.Count);
			Assert.AreEqual(2, model.Root.list[0].Value);

		}

		[Test]
		public void ReplaceChangeFromRemote() {
			Assert.AreEqual(4, model.Root.list.Value.Count);

			model.UpdateFromExternal("list", Change.Change.ReplaceChange(3, new List<long> { 10, 20 }));

			Assert.AreEqual(5, model.Root.list.Value.Count);
			Assert.AreEqual(1, model.Root.list[0].Value);
			Assert.AreEqual(10, model.Root.list[3].Value);
			Assert.AreEqual(20, model.Root.list[4].Value);
		}
	}
}
