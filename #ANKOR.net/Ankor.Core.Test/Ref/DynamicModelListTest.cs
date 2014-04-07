using System;
using System.Collections;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Collections.Generic;
using System.Linq;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.Ref {

	[TestFixture]
	public class DynamicModelListTest {
		private IInternalModel jModel;
		private DynaModel dModel;

		[SetUp]
		public void SetUp() {
			jModel = new RModel();
			//jModel = new JsonModel();

			var initialModel = TestUtils.ParseSnippet(jModel, @"{
				'n1'	: {
							'n2' : { 
										'nList' : [1, 2, 3, 4],
										'sList' : [null, '1', '2', '3', '4'],
										'emptyList' : []
										},							
							'dList' : [
								{
									'n1': 'v1',
									'n2':	4
								} 
							],
							'map' : {
									'k1' : 'v1',
									'k2' : 'v2',
									'k3' : 'v3'
							}
				}
			}");

			jModel.UpdateFromExternal("root", initialModel);

			dModel = new DynaModel(jModel);

		}

		[Test]
		public void ReadAsList() {
			jModel.PrintModelToDebug();
			IList list = dModel.Root.n1.n2.nList.Value;
			list.Should().HaveCount(4);			
			list[0].Should().BeOfType<long>();
			Assert.AreEqual(1L, list[0]);
			list[0].Should().Be(1L);
			list.Should().ContainInOrder(1L, 2L, 3L, 4L);
		}

		[Test]
		public void ReadAsStringList() {
			IList list = dModel.Root.n1.n2.sList.Value;
			list.Should().HaveCount(5);
			list[0].Should().BeNull();
			list[1].Should().BeOfType<string>();
			list[1].Should().Be("1");
			list.Should().Contain("1", "2", "3", "4");
		}

		[Test]
		public void ReadDeepList() {
			IList list = dModel.Root.n1.dList.Value;
			list.Should().HaveCount(1);
			IRef dict0 = (IRef) list[0];
			dict0.Should().BeOfType<IRef>();
			dict0.AppendPath("n1").Value.Should().Be("v1");

			dynamic dynDict = list[0];
			Assert.AreEqual("v1", dynDict.n1.Value);
		}

		[Test]
		public void EnumerateOnListRef() {
			IEnumerable<IRef> refs = dModel.Root.n1.n2.nList;
			refs.Should().HaveCount(4);

			refs.First().Value.Should().Be(1L);
			refs.First().Path.Should().Be("root.n1.n2.nList[0]");

			refs.Last().Path.Should().Be("root.n1.n2.nList[3]");
		}

		[Test]
		public void EnumerateOnDynamic() {
			string result = "";
			foreach (dynamic element in dModel.Root.n1.n2.nList) {
				result += element.Value;
			}
			result.Should().Be("1234");			
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void EnumerateOnWrongRefFails() {
			// this is not an array ref node, cannot enumerate this way
			IEnumerable<IRef> refs = dModel.Root.n1;
			var elem = refs.First();
		}

		[Test]
		public void ListPathSyntax() {
			dModel.RootRef.AppendPath("n1.n2.nList[0]").Value.Should().Be(1L);
			dModel.RootRef.AppendPath("n1.n2.nList[1]").Value.Should().Be(2L);
			dModel.RootRef.AppendPath("n1.n2.nList[1]").Path.Should().Be("root.n1.n2.nList[1]");

			Assert.AreEqual("2", dModel.Root.n1.n2.sList[2].Value);
			Assert.AreEqual("root.n1.n2.sList[2]", dModel.Root.n1.n2.sList[2].Path);
			
		}

		[Test]
		public void PropertyNames() {
			Assert.AreEqual("n2", dModel.Root.n1.n2.PropertyName);
			dModel.RootRef.AppendPath("n1.n2.nList").PropertyName.Should().Be("nList");

			dModel.RootRef.AppendPath("n1.n2.nList[1]").PropertyName.Should().Be("1");
			Assert.AreEqual("0", dModel.Root.n1.n2.nList[0].PropertyName);
		}


		[Test]
		public void EnumerateOnDictionaryRef() {
			// it is possible to enumerate over array ref nodes, so it should be for maps too?! (for the sake of consinstency)
			// but enumeration would not mean casting to IDictionary, but IEnumerable<KeyValuePair<TKey, TValue>>
			IEnumerable<KeyValuePair<string, IRef>> refs = dModel.Root.n1.map;

			foreach (var keyValuePair in refs) {
				
			}

			refs.First().Key.Should().Be("k1");
			refs.First().Value.Value.Should().Be("v1");
			
		}

		[Test]
		public void EnumerateOnDictionaryRefDynamic() {
			foreach (KeyValuePair<string, dynamic> e in dModel.Root.n1.map) {
				
			}
		}

		[Test]
		public void ReadAsDictionary() {
			IDictionary<string, IRef> dict = dModel.Root.n1.n2.Value;
			dict.Should().HaveCount(3);
			dict["nList"].Should().BeOfType<IRef>();
			Console.WriteLine(string.Join(", ", dict.Keys));			
			dict.Keys.Should().BeEquivalentTo(new [] {"nList", "sList", "emptyList"});

			dynamic nList = dict["nList"];
			Assert.AreEqual(1L, nList[0].Value);
		}

	}
}
