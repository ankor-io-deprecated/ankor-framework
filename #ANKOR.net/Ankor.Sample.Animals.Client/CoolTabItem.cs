using System.Windows.Controls;
using System.Windows.Input;

namespace Ankor.Sample.Animals.Client {
	public class CoolTabItem : TabItem {

		static CoolTabItem() {
			// attempt to use same style as parent tabitem
			// DefaultStyleKeyProperty.OverrideMetadata(typeof(CoolTabItem), new FrameworkPropertyMetadata(typeof(TabItem)));
		}
		public CoolTabItem() {
			this.MouseUp += CloseOnMiddleButton;
		}

		private void CloseOnMiddleButton(object sender, MouseButtonEventArgs e) {
			if (e.ButtonState == MouseButtonState.Released) {
				if (e.ChangedButton == MouseButton.Middle) {
					var tabItem = ((TabItem)sender);
					((TabControl)tabItem.Parent).Items.Remove(tabItem);
				}
			}

		}
	}
}