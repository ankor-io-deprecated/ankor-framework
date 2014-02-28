using System;
using System.Collections;
using System.Collections.Generic;
using System.Dynamic;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;


namespace Ankor.Core.Test.Ref {

	[TestFixture]
	public class PathEvaluatorTest {

		Evaluator evaler = new Evaluator();


		[Test]
		public void EvalSimpleProperty() {
			IDictionary<string, object> dict = new Dictionary<string, object>();
			dict["k1"] = "v1";
			dict["k2"] = 2L;			

			evaler.GetValue(dict, "k1").Should().Be("v1");
			evaler.GetValue(dict, "k2").Should().Be(2L);
		}

		[Test]
		public void EvalPathProperty() {
			dynamic root = new ExpandoObject();
			root.k1 = "v1";
			root.next = new ExpandoObject();
			root.next.k11 = "v11";
			root.next.k12 = 3L;

			Assert.AreEqual("v1", evaler.GetValue(root, "k1"));
			Assert.AreEqual("v11", evaler.GetValue(root, "next.k11"));

		}

		[Test]
		public void EvalPathIndexProperty() {
			dynamic root = new ExpandoObject();
			root.k1 = "v1";
			root.next = new ExpandoObject();
			root.next.k12 = 3L;
			root.next.list = new List<int>(new [] {1, 2, 3});

			Assert.AreEqual(2, evaler.GetValueSafe(root, "next.list[1]"));			
		}

		[Test]
		public void EvalIndexPropert() {
			dynamic root = new List<int>(new[] { 1, 2, 3 });

			Assert.AreEqual(2, evaler.GetValue(root, "[1]"));
		}

		[Test]
		public void EvalLongMixedPath() {
			dynamic root = new ExpandoObject();
			root.next = new ExpandoObject();
			root.next.k11 = "v11";
			root.next.k12 = 3L;
			dynamic e1 = new ExpandoObject();
			e1.val = "14";
			root.next.list = new ArrayList(new object[] { e1, null });

			Assert.AreEqual("14", evaler.GetValue(root, "next.list[0].val"));
		}

		[Test]
		public void EvalDeepIndexers() {
			dynamic root = new ArrayList(new object[] { new ArrayList(new object[] {4})});

			Evaluator e = new Evaluator(root);

			Assert.AreEqual(4, e.GetValue("[0][0]"));
		}

		[Test]
		public void EvalToRoot() {
			dynamic root = new ExpandoObject();

			Assert.AreEqual(root, evaler.GetValue(root, ""));
		}

		// TODO property not found exception would be nicer, improve error messages (expression path an pos of error)
		[Test]
		[ExpectedException(typeof(KeyNotFoundException), ExpectedMessage = "path.to.nowhere", MatchType = MessageMatch.Contains)]
		public void EvalToInvalidThrowsException() {
			dynamic root = new ExpandoObject();

			evaler.GetValue(root, "path.to.nowhere");
			
		}

		[Test]
		[ExpectedException(typeof(KeyNotFoundException), ExpectedMessage = "to", MatchType = MessageMatch.Contains)]
		public void EvalToInvalidThrowsException2() {
			dynamic root = new ExpandoObject();
			root.path = new ExpandoObject();

			evaler.GetValue(root, "path.to.nowhere");
		}

		[Test]
		public void EvalSafeReturnsNullInsteadOfException() {
			dynamic root = new ExpandoObject();

			Assert.IsNull(evaler.GetValueSafe(root, "path.to.nowhere"));
		}

		[Test]
		public void ReturnNullOnNullValue() {
			dynamic root = new ExpandoObject();
			root.k1 = "v1";
			root.k2 = null;

			Assert.IsNull(evaler.GetValue(root, "k2"));

		}

		[Test]
		public void SetValueSimple() {
			IDictionary<string, object> dict = new Dictionary<string, object>();
			dict["k1"] = "v1";

			evaler.SetValue(dict, "k1", "betterValue");
			evaler.SetValue(dict, "k2", 4);

			Assert.AreEqual("betterValue", dict["k1"]);
			Assert.AreEqual(4, dict["k2"]);
		}

		[Test]
		public void SetValueOnDeeperPathExpando() {
			dynamic root = new ExpandoObject();
			root.next = new ExpandoObject();
			root.next.k11 = "v11";

			evaler.SetValue(root, "next.k11", "betterValue");
			evaler.SetValue(root, "next.k12", 4);

			Assert.AreEqual("betterValue", root.next.k11);
			Assert.AreEqual(4, root.next.k12);
			
		}

		[Test]
		public void SetValueOnListIndex() {
			dynamic root = new List<int>(new[] { 1, 2, 3 });

			evaler.SetValue(root, "[1]", 4);

			Assert.AreEqual(4, root[1]);
		}

		[Test]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void SetValuOnInvalidListIndexFails() {
			dynamic root = new List<int>(new[] { 1, 2, 3 });

			evaler.SetValue(root, "[10]", 4);
		}
		



	}
}
