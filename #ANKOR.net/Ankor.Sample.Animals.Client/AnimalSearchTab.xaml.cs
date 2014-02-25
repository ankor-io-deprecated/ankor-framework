using System.Windows;
using System.Windows.Controls;
using Ankor.Core.Action;

namespace Ankor.Sample.Animals.Client {
	/// <summary>
	/// Interaction logic for AnimalSearchTab.xaml
	/// </summary>
	public partial class AnimalSearchTab : UserControl {
		public dynamic TabRef { get; private set; }

		public AnimalSearchTab(dynamic tabRef) {
			TabRef = tabRef;
			InitializeComponent();
		}

		private void Save(object sender, RoutedEventArgs e) {
			TabRef.model.Fire(new AAction("save"));
		}

		private void DeleteRow(object sender, RoutedEventArgs e) {
			TabRef.model.Fire(new AAction("delete").AddParam("uuid", (sender as Button).Tag));
		}

		private void EditRow(object sender, RoutedEventArgs e) {
			TabRef.model.Fire(new AAction("edit").AddParam("uuid", (sender as Button).Tag));
		}
	}
}
