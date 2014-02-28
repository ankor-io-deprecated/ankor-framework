using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;
using Ankor.Core.Action;
using Ankor.Core.Ref;

namespace Ankor.Sample.Todo.Client {
	/// <summary>
	/// Interaction logic for MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window {

		public dynamic RootRef { get; private set; }

		public MainWindow() {
			RootRef = App.Ankor.RefModel.RootRef;			

			RootRef.Fire("init");

			Thread.Sleep(200);
			// wait for init to complete, make this somehow nicer, but the list box data binding fails if no model is here.
			// maybe we need the auto create deep path on .net because of this
			
			InitializeComponent();

			// this is not very nice, but we cannot start dispaching to ui thread before initialize component has finished
			// because otherwise the client side model would not be complete and the binding will fail
			App.Ankor.Dispatcher = this.Dispatcher;

			//((DynaRef)RootRef.model.tasks).PropertyChanged += TasksChanged;
			
		}

//		void TasksChanged(object sender, System.ComponentModel.PropertyChangedEventArgs e) {
//			Console.WriteLine("CHANGED " + e.PropertyName);
//			if (e.PropertyName.EndsWith(".editing")) {
				//Dispatcher.BeginInvoke(new Action<string>(DoTaskChanged), e.PropertyName);
//				
//			}
//		}
//
//		private void DoTaskChanged(string path) {
//			string refPath = path.Substring(0, path.Length - ".editable".Length + 1);
//			dynamic changedTodoRef = RootRef.model.tasks[refPath];
//			Console.WriteLine("CHANGED ref" + changedTodoRef);
//			var ctrl = this.FindName("todoTextBox");
//			Console.WriteLine("found " + ctrl);
//		}

		private void BtnDebugClick(object sender, RoutedEventArgs e) {
			App.Ankor.PrintDebug();
		}

		private void TxtNewTaskKeyDown(object sender, KeyEventArgs e) {
			if (e.Key == Key.Enter) {
				if (!string.IsNullOrEmpty(txtNewTask.Text)) {
					RootRef.model.Fire(new AAction("newTask", paramKey: "title", paramVal: txtNewTask.Text));
					txtNewTask.Clear();
				}
			}
		}


		private void RemoveTaskClick(object sender, RoutedEventArgs e) {
			DynaRef currentItemRef = (DynaRef)((FrameworkElement)sender).Tag;

			var clickedItemIndex = int.Parse(currentItemRef.PropertyName);
			if (clickedItemIndex >= 0) {
				RootRef.model.Fire(new AAction("deleteTask", paramKey: "index", paramVal: clickedItemIndex));
			}
		}

		private void ClearCompleted(object sender, RoutedEventArgs e) {
			RootRef.model.Fire("clearTasks");
		}

		private void StartEditTodo(object sender, MouseButtonEventArgs e) {
			dynamic currentItemRef = ((FrameworkElement)sender).Tag;
			currentItemRef.editing = true;

			var textBox = ((TextBox) sender);
			textBox.IsReadOnly = false;
			textBox.Focusable = true;
			textBox.SelectAll();
			textBox.Focus();

		}

		private void LostEditTodoFocus(object sender, RoutedEventArgs e) {
			EndEditing(sender);
		}

		private void EnterEditTodo(object sender, KeyEventArgs e) {
			if (e.Key == Key.Enter) {
				EndEditing(sender);
				Keyboard.ClearFocus();
			}
		}

		private static void EndEditing(object sender) {
			dynamic currentItemRef = ((FrameworkElement) sender).Tag;
			currentItemRef.editing = false;

			var textBox = ((TextBox) sender);
			textBox.IsReadOnly = true;
			textBox.Focusable = false;
		}

		private void ToggleAll(object sender, RoutedEventArgs e) {
			RootRef.model.Fire(new AAction(name: "toggleAll", paramKey: "toggleAll", paramVal: btnToggleAll.IsChecked));
		}


		private void HandleFilterButton(object sender, RoutedEventArgs e) {
			DoToggleGroup(sender, btnFilterAll, btnFilterCompleted, btnFilterActive);
			e.Handled = true;
		}

		private void DoToggleGroup(object sender, params ToggleButton[] btns) {
			foreach (var btn in btns) {
				if (btn != null) {
					btn.IsChecked = btn == sender;
					(btn.Tag as dynamic).Value = btn.IsChecked;
				}
			}			
		}

	}
}
