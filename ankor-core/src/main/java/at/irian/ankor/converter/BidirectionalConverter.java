package at.irian.ankor.converter;

/**
 * @author Manfred Geiler
 */
public interface BidirectionalConverter<A,B> extends Converter<A,B> {
    A convertFrom(B b);
}
