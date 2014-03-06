package at.irian.ankor.messaging.json.common;

import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
* @author Manfred Geiler
*/
@Deprecated
public class MessageDeserializer extends StdDeserializer<Message> {

    public MessageDeserializer() {
        super(Message.class);
    }

    @Override
    public Message deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode tree = mapper.readTree(jp);
        Class<? extends Message> detectedType = null;
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = tree.fields();
        while (elementsIterator.hasNext())
        {
            Map.Entry<String, JsonNode> element=elementsIterator.next();
            String name = element.getKey();
            if (name.equals("action")) {
                detectedType = ActionMessage.class;
                break;
            }
            if (name.equals("change")) {
                detectedType = ChangeMessage.class;
                break;
            }
        }
        if (detectedType == null) {
            throw new JsonParseException("Cannot determine Message type", jp.getCurrentLocation());
        }
        return mapper.treeToValue(tree, detectedType);
    }
}
