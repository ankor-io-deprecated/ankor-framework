package at.irian.ankor.fx.binding.convert;

/**
* @author Manfred Geiler
*/
public class ReverseBooleanConverter implements RefValueConverter<Boolean,Boolean> {

    @Override
    public Boolean getConvertedValue(Boolean refValue) {
        return refValue != null ? !refValue : null;
    }

    private static final ReverseBooleanConverter INSTANCE = new ReverseBooleanConverter();

    public static ReverseBooleanConverter instance() {
        return INSTANCE;
    }

}
