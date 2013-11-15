package at.irian.ankor.fx.binding.convert;

import at.irian.ankor.converter.Converter;

/**
* @author Manfred Geiler
*/
public class SimpleEnumStringConverter implements Converter<Enum,String> {

    @Override
    public String convertTo(Enum anEnum) {
        return anEnum != null ? anEnum.name() : "";
    }


    private static final SimpleEnumStringConverter INSTANCE = new SimpleEnumStringConverter();

    public static SimpleEnumStringConverter instance() {
        return INSTANCE;
    }
}
