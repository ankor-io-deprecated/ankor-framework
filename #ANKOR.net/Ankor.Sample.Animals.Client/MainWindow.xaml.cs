using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Threading;
using Ankor.Core.Ref;

namespace Ankor.Sample.Animals.Client {
	/// <summary>
	/// Interaction logic for MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window {
		public IRef RootRef {get; private set;}
		private readonly IRef panelsRef;

		public MainWindow() {
			RootRef = App.Ankor.RefModel.RootRef;
			panelsRef = RootRef.AppendPath("contentPane.panels");
			panelsRef.PropertyChanged += OnPanelsChanged;

			InitializeComponent();

			RootRef.Fire("init");
		}

		private void RebuildPanels() {
			IDictionary<string, object> panels = (IDictionary<string, object>)this.panelsRef.Value;
			// easy for now, remove all and create new
			tabControl.Items.Clear();
			if (panels != null) {
				foreach (string panelName in panels.Keys) {
					ShowTab(panelsRef.AppendPath(panelName));
				}
			}

		}

		private void OnPanelsChanged(object sender, PropertyChangedEventArgs e) {
			string changePath = e.PropertyName;
			if (changePath == "Value") { // completely changed?
				Dispatcher.BeginInvoke(DispatcherPriority.Normal, new Action(RebuildPanels));

			} else if (!e.PropertyName.Contains(".")) { // dont go deeper, just myself
				dynamic tabRef = panelsRef.AppendPath(e.PropertyName);
				Dispatcher.BeginInvoke(DispatcherPriority.Normal, new Action<object>(ShowTab), tabRef);
			}
		}

		private void ShowTab(dynamic tabRef) {

			var item = new CoolTabItem();
			item.Content = CreateAnimalTabContent(tabRef);
			
			Binding headerBinding = new Binding();
			headerBinding.Path = new PropertyPath("Value");
			headerBinding.Source = tabRef.name;
			item.SetBinding(HeaderedContentControl.HeaderProperty, headerBinding);
			item.Name = tabRef.id.Value;

			tabControl.Items.Add(item);
			tabControl.SelectedItem = item;
		}

		private UserControl CreateAnimalTabContent(dynamic tabRef) {
			string tabType = tabRef.type.Value;
			if (tabType == "animalDetail") {
				return new AnimalDetailTab(tabRef);
			}
			if (tabType == "animalSearch") {
				return new AnimalSearchTab(tabRef);
			}
			throw new ArgumentException("unknow tab type " + tabRef.type);			
		}

		void OpenAnimalDetailTab(object sender, RoutedEventArgs e)		{			
			RootRef.AppendPath("contentPane").Fire("createAnimalDetailPanel");
		}

		private void OpenAnimalSearchTab(object sender, RoutedEventArgs e) {
			RootRef.AppendPath("contentPane").Fire("createAnimalSearchPanel");
		}

		private void bx_Click(object sender, RoutedEventArgs e) {
			this.boxUser.Text = "reset";
		}

		private void BtnDebugClick(object sender, RoutedEventArgs e) {
			App.Ankor.PrintDebug();
		}

		
	}
}
