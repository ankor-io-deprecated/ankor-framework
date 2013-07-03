package at.irian.ankor.system;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.LoopbackMessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageLoop;
import at.irian.ankor.messaging.SocketMessageLoop;
import at.irian.ankor.messaging.json.JsonMessageMapper;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.SingletonModelELRefContextFactory;
import at.irian.ankor.rmi.ELRemoteMethodActionEventListener;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;
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
                                RefContextFactory refContextFactory, EventListeners globalEventListeners,
                                String name,
                                RemoteMethodActionEventListener remoteMethodActionEventListener) {
        super(name, messageFactory, messageLoop.getMessageBus(), globalEventListeners,
              refContextFactory,
              remoteMethodActionEventListener);
        this.messageLoop = messageLoop;
    }


    public static SocketAnkorSystem create(String systemName, Class<?> modelType, BeanResolver beanResolver,
                                           String remoteHost, int remotePort, int localPort) {
        MessageFactory messageFactory = new MessageFactory();

        MessageLoop<String> messageLoop = new SocketMessageLoop<String>(systemName, new JsonMessageMapper(),
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

        return new SocketAnkorSystem(messageFactory, messageLoop, refContextFactory, globalEventListeners,
                                     systemName, null);
    }

    public SocketAnkorSystem withRemoteMethodActionListenerEnabled() {
        return new SocketAnkorSystem(getMessageFactory(),
                                     messageLoop,
                                     getRefContextFactory(), getGlobalEventListeners(),
                                     getSystemName(),
                                     new ELRemoteMethodActionEventListener());
    }

    @SuppressWarnings("UnusedDeclaration")
    public static SocketAnkorSystem create(String name, Class<?> modelType,
                                     final String singletonBeanName, final Object singletonBean) {
        return create(name, modelType, new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                if (beanName.equals(singletonBeanName)) {
                    return singletonBean;
                } else {
                    return null;
                }
            }
        }, null, 0, 0);
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
