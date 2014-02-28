using System.Windows;
using System.Windows.Controls;
using Ankor.Core.Action;

namespace Ankor.Sample.Animals.Client {
	/// <summary>
	/// Interaction logic for AnimalDetailTab.xaml
	/// </summary>
	public partial class AnimalDetailTab : UserControl {
		public dynamic TabRef { get; private set; }

		public AnimalDetailTab(dynamic tabRef) {
			TabRef = tabRef;
			InitializeComponent();
		}
		
		private void Save(object sender, RoutedEventArgs e) {
			TabRef.model.Fire(new AAction("save"));
		}
	}
}