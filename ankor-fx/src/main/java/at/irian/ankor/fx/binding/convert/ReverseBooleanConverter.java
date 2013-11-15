package at.irian.ankor.fx.binding.convert;

import at.irian.ankor.converter.BidirectionalConverter;

/**
* @author Manfred Geiler
*/
public class ReverseBooleanConverter implements BidirectionalConverter<Boolean,Boolean> {

    @Override
    public Boolean convertTo(Boolean b) {
        return b != null ? !b : null;
    }

    @Override
    public Boolean convertFrom(Boolean b) {
        return b != null ? !b : null;
    }


    private static final ReverseBooleanConverter INSTANCE = new ReverseBooleanConverter();

    public static ReverseBooleanConverter instance() {
        return INSTANCE;
    }

}
