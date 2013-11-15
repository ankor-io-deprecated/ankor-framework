package at.irian.ankor.converter;

/**
 * @author Manfred Geiler
 */
public interface Converter<A,B> {
    B convertTo(A a);
}
