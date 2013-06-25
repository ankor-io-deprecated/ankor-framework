package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.rmi.RemoteMethodAction;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class JsonMessageMapper implements MessageMapper<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMessageMapper.class);

    private final ObjectMapper mapper;

    public JsonMessageMapper() {
        SimpleModule module =
                new SimpleModule("PolymorphicMessageDeserializerModule",
                                 new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        module.addDeserializer(Action.class, new ActionDeserializer());
        //module.addSerializer(Ref.class, new RefSerializer());
        //module.addDeserializer(Ref.class, new RefDeserializer(refFactory));

        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(module);

        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@javaType");
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

    class ActionDeserializer extends StdDeserializer<Action> {

        ActionDeserializer() {
            super(Action.class);
        }

        @Override
        public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode tree = mapper.readTree(jp);
            Class<? extends Action> detectedType = null;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator = tree.fields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element=elementsIterator.next();
                String name = element.getKey();
                if (name.equals("methodExpression")) {
                    detectedType = RemoteMethodAction.class;
                    break;
                }
            }
            if (detectedType == null) {
                detectedType = SimpleAction.class;
            }
            return mapper.treeToValue(tree, detectedType);
        }
    }

//    class RefSerializer extends StdSerializer<Ref> {
//
//        RefSerializer() {
//            super(Ref.class);
//        }
//
//        @Override
//        public void serialize(Ref value, JsonGenerator jgen, SerializerProvider provider)
//                throws IOException {
//            jgen.writeString(value.path());
//        }
//    }
//
//    class RefDeserializer extends StdDeserializer<Ref> {
//
//        RefDeserializer() {
//            super(Ref.class);
//        }
//
//        @Override
//        public Ref deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
//            String path = mapper.readValue(jp, String.class);
//            return refFactory.ref(path);
//        }
//    }
}
