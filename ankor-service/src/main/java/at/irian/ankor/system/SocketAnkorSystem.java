package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.annotation.BeanAnnotationActionEventListener;
import at.irian.ankor.annotation.BeanAnnotationChangeEventListener;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.SingletonModelELRefContextFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Manfred Geiler
 */
public class SocketAnkorSystem extends AnkorSystem {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketAnkorSystem.class);

    private final MessageLoop<String> messageLoop;

    protected SocketAnkorSystem(MessageFactory messageFactory,
                                MessageLoop<String> messageLoop,
                                RefContextFactory refContextFactory,
                                EventListeners globalEventListeners,
                                String name,
                                ActionEvent.Listener remoteMethodActionEventListener,
                                ChangeEventListener annotationChangeEventListener) {
        super(name, messageFactory, messageLoop.getMessageBus(), globalEventListeners,
              refContextFactory,
              remoteMethodActionEventListener, annotationChangeEventListener);
        this.messageLoop = messageLoop;
    }


    public static SocketAnkorSystem create(String systemName, Class<?> modelType, BeanResolver beanResolver,
                                           String remoteHost, int remotePort, int localPort,
                                           boolean enableAnnotationListeners, MessageMapper<String> messageMapper) {
        MessageFactory messageFactory = new MessageFactory();

        MessageLoop<String> messageLoop = new SocketMessageLoop<String>(systemName, messageMapper,
                remoteHost, remotePort, localPort);

        EventListeners globalEventListeners = new ArrayListEventListeners();

        Config config = ConfigFactory.load();

        EventDelaySupport eventDelaySupport = new EventDelaySupport(systemName);

        RefContextFactory refContextFactory = SingletonModelELRefContextFactory.getInstance(config,
                                                                                            modelType,
                                                                                            globalEventListeners,
                                                                                            messageLoop.getMessageBus(),
                                                                                            beanResolver,
                                                                                            eventDelaySupport);
        ActionEvent.Listener annotationActionEventListener = null;
        ChangeEventListener annotationChangeEventListener = null;
        if (enableAnnotationListeners) {
            annotationActionEventListener = new BeanAnnotationActionEventListener(beanResolver);
            annotationChangeEventListener = new BeanAnnotationChangeEventListener(beanResolver, refContextFactory.createRefContext().pathSyntax());
        }

        return new SocketAnkorSystem(messageFactory, messageLoop, refContextFactory, globalEventListeners,
                                     systemName, annotationActionEventListener, annotationChangeEventListener);
    }

    @Override
    public void start() {
        if (!messageLoop.isConnected()) {
            throw new IllegalStateException("message loop is not connected");
        }
        super.start();
        messageLoop.start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoopbackMessageBus<String> getMessageBus() {
        return (LoopbackMessageBus<String>) super.getMessageBus();
    }

}
