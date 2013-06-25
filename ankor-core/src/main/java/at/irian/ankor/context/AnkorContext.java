package at.irian.ankor.context;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorContext {

    protected final ModelHolder modelHolder;
    protected final RefContext refContext;
    private Message currentRemoteMessage;

    public AnkorContext(ModelHolder modelHolder, RefContext refContext) {
        this.modelHolder = modelHolder;
        this.refContext = refContext;
    }

    public ModelHolder getModelHolder() {
        return modelHolder;
    }

    public RefFactory getRefFactory() {
        return refContext.getRefFactory();
    }

    public Message getCurrentRemoteMessage() {
        return currentRemoteMessage;
    }

    public void setCurrentRemoteMessage(Message currentRemoteMessage) {
        this.currentRemoteMessage = currentRemoteMessage;
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
