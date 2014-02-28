using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;

namespace Ankor.Wpf.Extensions {
	
	public class ReverseBooleanExtension :MarkupExtension {
		private static readonly ReverseBooleanConverter Converter = new ReverseBooleanConverter();

		public ReverseBooleanExtension() {}

		public override object ProvideValue(IServiceProvider serviceProvider) {
			return Converter;
		}
	}

	public class ReverseBooleanConverter : IValueConverter {
		public object Convert(object value, Type targetType, object parameter, CultureInfo culture) {
			return ((bool)value) != true; 
		}

		public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) {
			return ((bool)value) != true;
		}

		
	}
}
