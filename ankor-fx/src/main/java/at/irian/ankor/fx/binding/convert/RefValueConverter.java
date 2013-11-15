package at.irian.ankor.fx.binding.convert;

/**
 * @author Manfred Geiler
 */
public interface RefValueConverter<R, T> {

    T getConvertedValue(R refValue);

}
