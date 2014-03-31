package at.irian.ankor.serialization.json.simpletree;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.serialization.MessageMapper;
import at.irian.ankor.serialization.json.common.ActionDeserializer;
import at.irian.ankor.serialization.json.common.ChangeDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * MessageMapper that can map client-side generic (map-based) view data structures to json and vice versa.
 *
 * @author Manfred Geiler
 */
public class SimpleTreeJsonMessageMapper implements MessageMapper<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapper.class);

    private ObjectMapper mapper;

    public SimpleTreeJsonMessageMapper() {
        init();
    }

    public void init() {
        SimpleModule module =
                new SimpleModule("ViewDataJsonMessageMapperModule",
                                 new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Change.class, new ChangeDeserializer());
        module.addDeserializer(Action.class, new ActionDeserializer());

        mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.registerModule(module);

        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true); // importand for serializing maps that have keys like "com.foo.bar.Feature"

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@javaType");
    }

    @Override
    public String serialize(Object msg) {
        try {
            return mapper.writeValueAsString(msg);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot serialize " + msg, e);
        }
    }

    @Override
    public <M> M deserialize(String serializedMsg, Class<M> type) {
        try {
            return mapper.readValue(serializedMsg, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize " + serializedMsg, e);
        }
    }

}
