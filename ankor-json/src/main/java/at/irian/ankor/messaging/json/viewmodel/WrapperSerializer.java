package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.base.Wrapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
* @author Manfred Geiler
*/
class WrapperSerializer extends StdSerializer<Wrapper> {

    WrapperSerializer() {
        super(Wrapper.class);
    }

    @Override
    public void serialize(Wrapper wrapper, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Object value = wrapper.getWrappedValue();
        if (value != null) {
            jgen.writeObject(value);
        } else {
            jgen.writeNull();
        }
    }
}
