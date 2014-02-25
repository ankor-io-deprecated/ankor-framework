using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Dynamic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Markup;
using Ankor.Core.Messaging.Json;
using Ankor.Core.Ref;
using NUnit.Framework;
using FluentAssertions;
using Newtonsoft.Json.Linq;

namespace Ankor.Core.Test.DataBindings {

	/// <summary>
	/// Test data bindings defined in (inline) xaml from ui elements to the ankor model.
	/// The window has to be "shown" to trigger the binding to work. Need to have the [assembly: RequiresSTA] attribute.
	/// </summary>
	[TestFixture]
	public class XamlDataBindingTest {

		private IInternalModel jModel;
		private DynaModel dModel;

		[SetUp]
		public void SetUp() {

			jModel = new RModel();

			var initialModel = TestUtils.ParseSnippet(jModel, @"{
				'path'	: {
							'to' : { 
										'persons' : [
												{'name'	: 'david',		'age' : 14},
												{'name'	: 'schurl',		'age' : 24},
												{'name'	: 'sepp',			'age' : 34},
											]
								}							
				 },
				'userName' : 'numpf'
			}");

			jModel.UpdateFromExternal("", initialModel);

			dModel = new DynaModel(jModel);
		}

		[Test]
		public void BindSimpleLabel() {
			Window window = (Window)XamlReader.Parse(@"
				<Window x:Class=""System.Windows.Window"" x:Name=""this""
					xmlns='http://schemas.microsoft.com/winfx/2006/xaml/presentation'
					xmlns:x=""http://schemas.microsoft.com/winfx/2006/xaml""			
					DataContext=""{Binding ElementName=this, Path=Tag}""
				>
				
					<Label Content=""{Binding Path=userName.Value}""  Name='label1'  Width='60' />
				</Window>");
			window.Tag = dModel.DRef;

			window.Show();

			window.Should().NotBeNull();

			Label l = (Label)window.Content;
			l.Name.Should().Be("label1");
			l.Content.Should().Be("numpf");

			window.Close();

		}

		[Test]
		public void BindDataGrid() {
			Window window = (Window) XamlReader.Parse(@"
				<Window x:Class=""System.Windows.Window"" x:Name=""this""
					xmlns='http://schemas.microsoft.com/winfx/2006/xaml/presentation'
					xmlns:x=""http://schemas.microsoft.com/winfx/2006/xaml""			
					DataContext=""{Binding ElementName=this, Path=Tag}""
				>
					<DataGrid Name=""grid"" ItemsSource=""{Binding Path=path.to.persons.Value}"" 
										AutoGenerateColumns=""False"" CanUserAddRows=""False"" >
						<DataGrid.Columns>
							<DataGridTextColumn Header=""Name""		Binding=""{Binding name}"" />
							<DataGridTextColumn Header=""Age""		Binding=""{Binding age}"" />												
						</DataGrid.Columns>
					</DataGrid>
				
				</Window>");
			// TODO bind on .Value or not (dref or not on lists?)

			window.Tag = dModel.DRef;

			window.Show();

			DataGrid grid = (DataGrid)window.FindName("grid");
			object ob = dModel.DRef.path.to.persons.Value;
			grid.Items.Count.Should().Be(3);

			DataGridRow row = GetGridRow(1, grid);

			GetColumn(0, row, grid).Text.Should().Be("schurl");
			GetColumn(1, row, grid).Text.Should().Be("24");


			window.Close();

			
		}

		private static TextBlock GetColumn(int colIdx, DataGridRow row, DataGrid grid) {
			return (grid.Columns[colIdx].GetCellContent(row) as TextBlock);
		}

		private static DataGridRow GetGridRow(int rowIdx, DataGrid grid) {
			return (DataGridRow)grid.ItemContainerGenerator.ContainerFromIndex(rowIdx);
		}
	}
}
