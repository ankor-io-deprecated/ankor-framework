package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.service.rma.RemoteMethodAction;
import org.codehaus.jackson.*;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class JsonMessageMapper implements MessageSerializer<String>, MessageDeserializer<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMessageMapper.class);

    private final ObjectMapper mapper;

    public JsonMessageMapper() {
        SimpleModule module =
                new SimpleModule("PolymorphicMessageDeserializerModule",
                                 new Version(1, 0, 0, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        module.addDeserializer(Action.class, new ActionDeserializer());
        module.addSerializer(Ref.class, new RefSerializer());

        mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(module);
    }

    @Override
    public String serialize(Message msg) {
        try {
            return mapper.writeValueAsString(msg);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot serialize " + msg, e);
        }
    }

    @Override
    public Message deserialize(String serializedMsg) {
        try {
            return mapper.readValue(serializedMsg, Message.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize " + serializedMsg, e);
        }
    }


    class MessageDeserializer extends StdDeserializer<Message> {

        MessageDeserializer() {
            super(Message.class);
        }

        @Override
        public Message deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode root = (ObjectNode) mapper.readTree(jp);
            Class<? extends Message> clazz = null;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                    root.getFields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element=elementsIterator.next();
                String name = element.getKey();
                if (name.equals("action")) {
                    clazz = ActionMessage.class;
                    break;
                }
                if (name.equals("change")) {
                    clazz = ChangeMessage.class;
                    break;
                }
            }
            if (clazz == null) {
                throw new JsonParseException("Cannot determine Message type", jp.getCurrentLocation());
            }
            return mapper.readValue(root, clazz);
        }
    }

    class ActionDeserializer extends StdDeserializer<Action> {

        ActionDeserializer() {
            super(Action.class);
        }

        @Override
        public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode root = (ObjectNode) mapper.readTree(jp);
            Class<? extends Action> clazz = null;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                    root.getFields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element=elementsIterator.next();
                String name = element.getKey();
                if (name.equals("methodExpression")) {
                    clazz = RemoteMethodAction.class;
                    break;
                }
            }
            if (clazz == null) {
                clazz = SimpleAction.class;
            }
            return mapper.readValue(root, clazz);
        }
    }

    class RefSerializer extends SerializerBase<Ref> {

        RefSerializer() {
            super(Ref.class);
        }

        @Override
        public void serialize(Ref value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.path());
        }
    }

}
