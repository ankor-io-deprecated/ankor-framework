package at.irian.ankor.big.json;

import at.irian.ankor.big.modify.MapToBigMapDummyConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class BigMapSerializer extends StdSerializer<Map> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigListSerializer.class);

    private final MapToBigMapDummyConverter converter;

    public BigMapSerializer(MapToBigMapDummyConverter converter) {
        super(Map.class);
        this.converter = converter;
    }

    @Override
    public void serialize(Map actualMap, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (actualMap == null) {
            jgen.writeNull();
        } else {
            Map converted = converter.convert(actualMap);
            jgen.writeObject(converted);
        }
    }
}
