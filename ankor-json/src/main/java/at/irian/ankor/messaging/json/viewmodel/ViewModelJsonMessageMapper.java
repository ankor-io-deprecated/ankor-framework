package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.action.Action;
import at.irian.ankor.base.Wrapper;
import at.irian.ankor.big.json.AnkorSerializerModifier;
import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageArrayDeserializer;
import at.irian.ankor.messaging.MessageArraySerializer;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.json.common.ActionDeserializer;
import at.irian.ankor.messaging.json.common.ChangeDeserializer;
import at.irian.ankor.messaging.json.common.MessageDeserializer;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Arrays;

/**
 * MessageMapper that can map server-side strongly typed(!) view model objects to json and vice versa.
 *
 * @author Manfred Geiler
 */
public class ViewModelJsonMessageMapper implements MessageMapper<String>,
                                                   MessageArraySerializer<String>,
                                                   MessageArrayDeserializer<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapper.class);

    private static final Class<? extends Message[]> MESSAGE_ARRAY_TYPE = (new Message[0]).getClass();

    private final ObjectMapper mapper;

    public ViewModelJsonMessageMapper(BeanMetadataProvider beanMetadataProvider) {
        this.mapper = createMapper(beanMetadataProvider);
    }

    private static ObjectMapper createMapper(BeanMetadataProvider beanMetadataProvider) {
        SimpleModule module = new SimpleModule("ViewModelJsonMessageMapperModule",
                                               new Version(1, 0, 0, null, null, null));

        // custom serializers/deserializers
        module.addDeserializer(Message.class, new MessageDeserializer());
        module.addDeserializer(Action.class, new ActionDeserializer());
        module.addDeserializer(Change.class, new ChangeDeserializer());
        module.addSerializer(Wrapper.class, new WrapperSerializer());

        // do always ignore Refs (ie. do never serialize Refs)
        module.setMixInAnnotation(TypedRef.class, IgnoreMixIn.class);
        module.setMixInAnnotation(Object.class, DefaultMixIn.class);

        module.setSerializerModifier(new AnkorSerializerModifier(beanMetadataProvider));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);

        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true); // importand for serializing maps that have keys like "com.foo.bar.Feature"

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@javaType");

        return mapper;
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


    @Override
    public String serializeArray(Message[] messages) {
        try {
            return mapper.writeValueAsString(messages);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot serialize " + Arrays.toString(messages), e);
        }
    }

    @Override
    public Message[] deserializeArray(String serializedMessages) {
        try {
            return mapper.readValue(serializedMessages, MESSAGE_ARRAY_TYPE);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize " + serializedMessages, e);
        }
    }
}
