using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Windows.Data;
using System.Windows.Markup;

namespace Ankorman.Client.Wpf.Ankor.Wpf {

	public class DebugExtension : MarkupExtension {
		private static readonly DebugConverter Converter = new DebugConverter();

		public DebugExtension() { }

		public override object ProvideValue(IServiceProvider serviceProvider) {
			return Converter;
		}
	}

	public class DebugConverter : IValueConverter  {
		public object Convert(object value, Type targetType, object parameter, CultureInfo culture) {
			Console.WriteLine("convert {0} of type {1} with param {2}", value, targetType, parameter);
			return value;
		}

		public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) {
			Console.WriteLine("convert back {0} of type {1} with param {2}", value, targetType, parameter);
			return value;
		}
	}
}
