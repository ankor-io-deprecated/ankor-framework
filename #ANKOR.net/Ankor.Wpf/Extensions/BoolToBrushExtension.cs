using System;
using System.Drawing;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;

namespace Ankor.Wpf.Extensions {
	
	public class BoolToBrushExtension : MarkupExtension {

		[ConstructorArgument("trueBrush")]
		public Brush TrueBrush { get; set; }

		[ConstructorArgument("falseBrush")]
		public Brush FalseBrush { get; set; }

		public BoolToBrushExtension() : this(Brushes.Green, Brushes.LightGray) {}

		public BoolToBrushExtension(Brush trueBrush, Brush falseBrush) {
			TrueBrush = trueBrush;
			FalseBrush = falseBrush;
		}

		public override object ProvideValue(IServiceProvider serviceProvider) {
			var converter = new BooleanToBrushConverter();
			converter.Extension = this;
			return converter;
		}
	}

	public class BooleanToBrushConverter : IValueConverter {
		internal BoolToBrushExtension Extension = new BoolToBrushExtension();

		public object Convert(object value, Type targetType, object parameter, CultureInfo culture) {
			if ((bool)value) {
				return Extension.TrueBrush;
			} else {
				return Extension.FalseBrush;
			}			
		}

		public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) {
			return false;
		}

		
	}
}
