using System;
using System.Collections;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using System.Linq;
using System.Linq.Expressions;
using Ankor.Core.Action;
using Ankor.Core.Messaging;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using Ankor.Core.Test.Ref;
using Ankor.Core.Utils;
using FluentAssertions;
using NUnit.Framework;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.Messaging {

	[TestFixture]
	public class MessageMapperTest {

		private readonly JsonMessageMapper mapper = new JsonMessageMapper();
		private readonly MessageFactory messageFactory;

		public MessageMapperTest() {
			messageFactory = new MessageFactory("testSystem");
			messageFactory.CreateNextId = () => "testClient#1";					
			messageFactory.ModelContextId = "1";
		}

		private string GetReferenceMsg(string name) {
			var basePath = @"..\..\..\..\ankor-json\src\test\resources\at\irian\ankor\messaging\json\simpletree\referenceMsg_";
			var file = new FileInfo(basePath + name + ".json");
			string refMsg = file.Read();
			if (string.IsNullOrEmpty(refMsg)) {
				throw new ArgumentException("error reading reference message from " + file.FullName);
			}
			return refMsg;
		}

		[Test]
		public void TestSerializeSimpleChange() {
			var msg = (ChangeMessage) messageFactory.CreateChangeMessage("changed.path", Change.Change.ValueChange(14L));
			string json = mapper.Serialize(msg);

			Console.WriteLine(json);

			string expectedJson = GetReferenceMsg("simpleChange");
				
			AreJsonEqual(expectedJson, json);

			var msg2 = mapper.Deserialize<ChangeMessage>(json);
			msg2.Property.Should().Be(msg.Property);
			msg2.Change.Should().Be(msg.Change);

		}

		Evaluator evaluator = new Evaluator();

		[Test]
		public void TestDeSerializeComplexChange() {
			var msg = mapper.Deserialize<ChangeMessage>(GetReferenceMsg("complexChange"));
			//dynamic dmsg = msg.Change.Value;
			String val = (string) evaluator.GetValue(msg.Change.Value, "model.selectItems.types[0]");
			val.Should().Be("Fish");

			
			object first = evaluator.GetValue(msg.Change.Value, "model.animals.paginator.first");
			first.Should().Be(0L);

			msg.Change.Value.Should().BeAssignableTo<IDictionary<string, object>>();
		}

		[Test]
		public void TestSerializeSimpleAction() {
			
			var msg = messageFactory.CreateActionMessage("root.next", new AAction("init"));
			var json = mapper.Serialize(msg);


			string expectedJson = GetReferenceMsg("simpleAction");
				//@"{senderId:""testSystem"",modelId:""1"",messageId:""testClient#1"",property:""root.next"",action:{name:""init""}}";

//				JsonConvert.DeserializeObject(expectedJson, typeof(SortedDictionary<string, object>));
//			Console.WriteLine(string.Join(", ", dict.Select(e => e.Key + ": " + e.Value + "(" + e.Value.GetType() + ")"))) ;
//
//			dict = (IDictionary<string, object>)
//				JsonConvert.DeserializeObject(json, typeof(SortedDictionary<string, object>));
//			Console.WriteLine(string.Join(", ", dict.Select(e => e.Key + ": " + e.Value + "(" + e.Value.GetType() + ")"))) ;
//
			json.Should().NotBeNull();
			AreJsonEqual(expectedJson, json);

			Console.WriteLine(json);

			Message outMsg = mapper.Deserialize<ActionMessage>(json);
			outMsg.Should().BeOfType<ActionMessage>();

			var amsg = outMsg as ActionMessage;
			amsg.ModelId.Should().Be("1");
			amsg.MessageId.Should().Be("testClient#1");
			amsg.SenderId.Should().Be("testSystem");

			amsg.Action.Name.Should().Be("init");
			amsg.Action.Params.Should().BeNull();

			//outMsg.ShouldHave<ActionMessage>();
			//outMsg.As<ChangeMessage>().Change.Should().NotBeNull();

		}

		[Test]
		public void TestDataTypesAction() {

			var parms = new Dictionary<string, object>();
			parms["stringParam"] = "test";
			parms["intParam"] = 1;
			//parms["dateParam"] = new DateTime(10000); // TODO this serialization as millis might not be sufficent
			parms["booleanParam"] = true;
			parms["arrayParam"] = new int[] { 1, 2, 3, 4 };
			parms["listParam"] = new List<int> { 1, 2, 3, 4 };
			parms["doubleParam"] = 8.8d;

			string expectedJson = GetReferenceMsg("multiParamsAction");
//        @"{senderId:""testSystem"",modelId:""1"",messageId:""testClient#1"",property:""root.next"",
//					action:{name:""initParams"",params:{stringParam:""test"",intParam:1,booleanParam:true,
//					arrayParam:[1,2,3,4],listParam:[1,2,3,4],doubleParam:8.8}}}";

			string json = mapper.Serialize(CreateActionMessage("initParams", parms));

			Console.WriteLine(json);

			AreJsonEqual(expectedJson, json);

			var outMsg = mapper.Deserialize<ActionMessage>(json);
			outMsg.Should().BeOfType<ActionMessage>();

			var amsg = outMsg as ActionMessage;			
			amsg.Action.Params["stringParam"].Should().Be("test");
			amsg.Action.Params.Count.Should().Be(6);
			amsg.Action.Params["arrayParam"].Should().BeAssignableTo<IList<object>>();

		}

		private Message CreateActionMessage(String name, IDictionary<string, object> parms) {
				return messageFactory.CreateActionMessage("root.next", new AAction(name, parms));
		}		
		
		void AreJsonEqual(string expected, string actual) {
			if (expected != null && actual != null && !expected.Equals(actual)) {

				var expectedJ = (JObject) JsonConvert.DeserializeObject(expected);				
				var actualJ = (JObject) JsonConvert.DeserializeObject(actual);

				// ATTENTION: JSON is not ordered (insertion order would be nice, as over the wire)
				Sort(expectedJ);
				Sort(actualJ);
				
				Assert.AreEqual(expectedJ.ToString(), actualJ.ToString());
			}
			
		}

		static void Sort(JObject jObj) {
			if (jObj == null) {
				return;
			}
			var props = jObj.Properties().ToList();
			
			foreach (var prop in props) {
				prop.Remove();
			}

			foreach (var prop in props.OrderBy(p=>p.Name)) {
				jObj.Add(prop);
				Sort(prop.Value as JObject);
			}
		}

	}
}
