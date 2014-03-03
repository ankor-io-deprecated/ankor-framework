package at.irian.ankor.messaging;

import at.irian.ankor.system.AnkorSystem;

import java.lang.reflect.Constructor;

/**
 * @author Manfred Geiler
 */
public class MessageMapperFactory<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageMapperFactory.class);

    private final AnkorSystem ankorSystem;

    public MessageMapperFactory(AnkorSystem ankorSystem) {
        this.ankorSystem = ankorSystem;
    }

    @SuppressWarnings("unchecked")
    public MessageMapper<T> createMessageMapper() {
        String messageMapperType = ankorSystem.getConfig().getString("at.irian.ankor.messaging.MessageMapper");

        Class<MessageMapper> type;
        try {
            type = (Class<MessageMapper>) Class.forName(messageMapperType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find MessageMapper type " + messageMapperType, e);
        }

        try {
            Constructor<MessageMapper> constructor = type.getConstructor(AnkorSystem.class);
            try {
                return constructor.newInstance(ankorSystem);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to create instance of type " + type, e);
            }
        } catch (NoSuchMethodException e) {
            try {
                Constructor<MessageMapper> defaultConstructor = type.getConstructor();
                try {
                    return defaultConstructor.newInstance();
                } catch (Exception e1) {
                    throw new IllegalStateException("Unable to create instance of type " + type, e);
                }
            } catch (NoSuchMethodException e1) {
                throw new IllegalArgumentException("MessageMapper of type " + messageMapperType + " has no appropriate constructor", e);
            }
        }

    }
}
