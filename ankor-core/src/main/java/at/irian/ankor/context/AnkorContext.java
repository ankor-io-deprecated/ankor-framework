package at.irian.ankor.context;

import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorContext {

    private final ModelHolder modelHolder;
    private final RefContext refContext;
    private final MessageSender messageSender;

    public AnkorContext(ModelHolder modelHolder,
                        RefContext refContext,
                        MessageSender messageSender) {
        this.modelHolder = modelHolder;
        this.refContext = refContext;
        this.messageSender = messageSender;
    }

    public ModelHolder getModelHolder() {
        return modelHolder;
    }

    public RefFactory getRefFactory() {
        return refContext.getRefFactory();
    }

    public AnkorContext withMessageSender(MessageSender messageSender) {
        return new AnkorContext(modelHolder, refContext, messageSender);
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public PathSyntax getPathSyntax() {
        return refContext.getPathSyntax();
    }

    private static final ThreadLocal<AnkorContext> THREAD_INSTANCE = new ThreadLocal<AnkorContext>() {
        @Override
        protected AnkorContext initialValue() {
            return null;
        }
    };

    public static void setCurrentInstance(AnkorContext ankorContext) {
        if (ankorContext != null) {
            THREAD_INSTANCE.set(ankorContext);
        } else {
            THREAD_INSTANCE.remove();
        }
    }

    public static AnkorContext getCurrentInstance() {
        return THREAD_INSTANCE.get();
    }

}
