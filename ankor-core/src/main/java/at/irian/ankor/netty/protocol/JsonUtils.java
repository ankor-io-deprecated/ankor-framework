package at.irian.ankor.netty.protocol;

import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
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
 * <p>Contains JSON related functions that are useful for both encoders and decoders.</p>
 *
 */
public final class JsonUtils {

    public static ObjectMapper configureJsonMapper() {
        SimpleModule module =
                new SimpleModule("PolymorphicMessageDeserializerModule",
                        new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        //module.addDeserializer(Action.class, new ActionDeserializer());
        //module.addSerializer(Ref.class, new RefSerializer());
        //module.addDeserializer(Ref.class, new RefDeserializer(refFactory));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(module);

        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@javaType");

        return mapper;
    }

    private static class MessageDeserializer extends StdDeserializer<Message> {

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

//    private static class ActionDeserializer extends StdDeserializer<Action> {
//
//        ActionDeserializer() {
//            super(Action.class);
//        }
//
//        @Override
//        public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
//            ObjectNode tree = mapper.readTree(jp);
//            Class<? extends Action> detectedType = null;
//            Iterator<Map.Entry<String, JsonNode>> elementsIterator = tree.fields();
//            while (elementsIterator.hasNext())
//            {
//                Map.Entry<String, JsonNode> element=elementsIterator.next();
//                String name = element.getKey();
//                if (name.equals("params")) { // TODO find a better way
//                    detectedType = SimpleParamAction.class;
//                    break;
//                }
//            }
//            if (detectedType == null) {
//                detectedType = Action.class;
//            }
//            return mapper.treeToValue(tree, detectedType);
//        }
//    }


}
