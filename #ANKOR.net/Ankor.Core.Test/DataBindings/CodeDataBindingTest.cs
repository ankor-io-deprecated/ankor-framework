using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Dynamic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.DataBindings {

	/// <summary>
	/// Test data binding (defined in code) from ui elements to the ankor model.
	/// Need to have the [assembly: RequiresSTA] attribute.
	/// </summary>
	[TestFixture]
	public class CodeDataBindingTest {
		private IInternalModel iModel;
		private DynaModel dModel;
		private ComboBox cmb;

		[SetUp]
		public void SetUp() {

			//jModel = new JsonModel();
			iModel = new RModel();
			var initialModel = TestUtils.ParseSnippet(iModel, @"{
				'n1'	: {
							'n2' : { 
										'n3' : {
													'username'	: 'david',
													'age'				: 14
													}
										},
							'alist' : ['a', 'b', 'c']
							},
				'v1' : 'c'
			}");

			iModel.UpdateFromExternal("", initialModel);

			dModel = new DynaModel(iModel);
		}

		private Binding CreateBinding(object source, string path) {
			Binding binding = new Binding();			
			//binding.Mode = BindingMode.TwoWay; is default anyhow
			binding.UpdateSourceTrigger = UpdateSourceTrigger.PropertyChanged;
			binding.Source = source;
			binding.Path = new PropertyPath(path);
			return binding;
		}		

		[Test]
		public void BindTextBoxToRef() {

			Binding binding = CreateBinding(dModel.DRef, "v1.Value");
			TextBox box = new TextBox();
			box.SetBinding(TextBox.TextProperty, binding);

			Assert.AreEqual("c", dModel.DRef.v1.Value);
			Assert.AreEqual("c", box.Text);

			box.Text = "hubert";

			Assert.AreEqual("hubert", dModel.DRef.v1.Value);
			dModel.DRef.v1.Value = "josef";
			Assert.AreEqual("josef", box.Text);
		}

		[Test]
		public void BindTextBoxToRefDeepPath() {

			TextBox box = new TextBox();
			var binding = CreateBinding(dModel.DRef, "n1.n2.n3.username.Value");
			box.SetBinding(TextBox.TextProperty, binding);

			Assert.AreEqual("david", box.Text);

			box.Text = "hubert";
			Assert.AreEqual("hubert", dModel.DRef.n1.n2.n3.username.Value);

			dModel.DRef.n1.n2.n3.username.Value = "josef";
			Assert.AreEqual("josef", box.Text);
		}

		[Test]
		public void BindTextBoxToRefValue() {

			TextBox box = new TextBox();
			var binding = CreateBinding(dModel.DRef.n1.n2.n3.username, "Value");
			box.SetBinding(TextBox.TextProperty, binding);

			Assert.AreEqual("david", box.Text);

			box.Text = "hubert";
			Assert.AreEqual("hubert", dModel.DRef.n1.n2.n3.username.Value);

			dModel.DRef.n1.n2.n3.username.Value = "josef";
			Assert.AreEqual("josef", box.Text);
		}

		[Test]
		public void BindCombBoxSelectedItemToRefDeepPath() {

			cmb = new ComboBox();
			cmb.SetBinding(ItemsControl.ItemsSourceProperty, CreateBinding(dModel.DRef, "n1.alist.Value"));
			cmb.SetBinding(Selector.SelectedItemProperty, CreateBinding(dModel.DRef, "v1.Value"));

			AssertCmbBinding(dModel,
				model => model.DRef.v1.Value,
				(model, val) => model.DRef.v1.Value = val);

		}

		[Test]
		public void BindComboBoxSelectedItemToRefValue() {

			cmb = new ComboBox();
			cmb.SetBinding(ItemsControl.ItemsSourceProperty, CreateBinding(dModel.DRef, "n1.alist.Value"));
			cmb.SetBinding(Selector.SelectedItemProperty, CreateBinding(dModel.DRef.v1, "Value"));

			AssertCmbBinding((object)dModel.DRef,
				root => root.v1.Value,
				(root, val) => root.v1.Value = val
			);

			Assert.AreEqual("a", cmb.Items[0]);
			cmb.Items.Should().HaveCount(3);
		}

		/// <summary>
		/// Keep test as a reference to work with expandos
		/// </summary>
		[Test]
		public void BindComboBoxSelectedItemToExpando() {
			dynamic eo1 = new ExpandoObject();
			dynamic eo2 = new ExpandoObject();
			dynamic eo3 = new ExpandoObject();
			eo1.p1 = eo2;
			eo1.p1.p2 = eo3;
			eo1.p1.p2.Value = "c";

			cmb = new ComboBox();

			cmb.SetBinding(ItemsControl.ItemsSourceProperty, CreateBinding(dModel.DRef, "n1.alist.Value"));
			cmb.SetBinding(Selector.SelectedItemProperty, CreateBinding(eo1, "p1.p2.Value"));

			AssertCmbBinding((object)eo1,
				source => source.p1.p2.Value,
				(source, val) => source.p1.p2.Value = val 
			);
		}

		private void AssertCmbBinding(object source, Func<dynamic, object> getValue, Action<dynamic, object> setValue) {
			Assert.AreEqual("a", cmb.Items[0]);
			cmb.Items.Should().HaveCount(3);

			// Set via the index
			Console.WriteLine("****");
			cmb.SelectedIndex = 1;
			Console.WriteLine("****");

			// check cmb props
			cmb.SelectedItem.Should().Be("b");
			cmb.SelectedValue.Should().Be("b");
			// check binding to object
			Assert.AreEqual("b", getValue(source));

			// try the same again with next index
			cmb.SelectedIndex = 2;
			cmb.SelectedItem.Should().Be("c");
			cmb.SelectedValue.Should().Be("c");
			Assert.AreEqual("c", getValue(source));

			// now set the bound object
			setValue(source, "a");
			// and check if cmb is up to date
			cmb.SelectedItem.Should().Be("a");
			cmb.SelectedValue.Should().Be("a");
			cmb.SelectedIndex.Should().Be(0);
		}

		// TODO test somewhere not on a leaf: map (== dynamic) oder array/list

	}


	// THERE seem to be 2 options: create a ref for every navigation step, or create just on .Ref 
	// the ref always just delegates to the refContext == jModel in both directions. 
	// listen to a ref means listen to the refContext in reality
}
