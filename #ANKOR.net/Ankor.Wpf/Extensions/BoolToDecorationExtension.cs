using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;
using System.Windows.Markup;

namespace Ankor.Wpf.Extensions {
	
	public class BoolToDecorationExtension :MarkupExtension {

		[ConstructorArgument("decoration")]
		public TextDecorationCollection Decoration { get; set; }

		public BoolToDecorationExtension() {
		  Decoration = TextDecorations.Underline;
		}

		public override object ProvideValue(IServiceProvider serviceProvider) {
			var converter = new BooleanDecorationConverter();
			converter.Extension = this;
			return converter;
		}
	}

	public class BooleanDecorationConverter : IValueConverter {
		internal BoolToDecorationExtension Extension = new BoolToDecorationExtension();

		public object Convert(object value, Type targetType, object parameter, CultureInfo culture) {
			if ((bool)value) {
				return Extension.Decoration;
			}
			return null;
		}

		public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) {
			return false;
		}

		
	}
}
