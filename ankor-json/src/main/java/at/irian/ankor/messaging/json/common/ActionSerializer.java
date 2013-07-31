package at.irian.ankor.messaging.json.common;

import at.irian.ankor.action.Action;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

/**
* @author Manfred Geiler
*/
public class ActionSerializer extends StdSerializer<Action> {

    public ActionSerializer() {
        super(Action.class);
    }

    @Override
    public void serialize(Action action, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Map<String,Object> params = action.getParams();
        if (params == null || params.isEmpty()) {
            jgen.writeString(action.getName());
        } else {
            jgen.writeStartObject();
            jgen.writeStringField("name", action.getName());
            jgen.writeObjectField("params", action.getParams());
            jgen.writeEndObject();
        }
    }
}
