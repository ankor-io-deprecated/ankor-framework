package at.irian.ankor.big.json;

import at.irian.ankor.big.modify.ListToBigListDummyConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class BigListSerializer extends StdSerializer<Collection> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigListSerializer.class);

    private final ListToBigListDummyConverter converter;

    public BigListSerializer(ListToBigListDummyConverter converter) {
        super(Collection.class);
        this.converter = converter;
    }

    @Override
    public void serialize(Collection actualCollection, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (actualCollection == null) {
            jgen.writeNull();
        } else {
            Collection converted = converter.convert(actualCollection);
            jgen.writeObject(converted);
        }
    }
}
