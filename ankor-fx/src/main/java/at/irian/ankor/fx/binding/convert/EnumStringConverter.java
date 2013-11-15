package at.irian.ankor.fx.binding.convert;

/**
* @author Manfred Geiler
*/
public class EnumStringConverter implements RefValueConverter<Enum,String> {

    @Override
    public String getConvertedValue(Enum refValue) {
        return refValue != null ? refValue.name() : "";
    }

    private static final EnumStringConverter INSTANCE = new EnumStringConverter();

    public static EnumStringConverter instance() {
        return INSTANCE;
    }
}
