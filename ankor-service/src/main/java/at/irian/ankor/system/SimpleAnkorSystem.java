package at.irian.ankor.system;

import at.irian.ankor.context.AnkorContextFactory;
import at.irian.ankor.context.SingletonInstanceAnkorContextFactory;
import at.irian.ankor.el.BeanResolverELResolver;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.ListenersHolder;
import at.irian.ankor.event.UnsynchronizedListenersHolder;
import at.irian.ankor.messaging.LoopbackMessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageLoop;
import at.irian.ankor.messaging.json.JsonMessageLoop;
import at.irian.ankor.rmi.ELRemoteMethodActionEventListener;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorSystem extends AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorSystem.class);

    private final MessageLoop<String> messageLoop;

    protected SimpleAnkorSystem(MessageFactory messageFactory,
                                MessageLoop<String> messageLoop,
                                ListenersHolder listenersHolder,
                                AnkorContextFactory ankorContextFactory,
                                String name,
                                RemoteMethodActionEventListener remoteMethodActionEventListener) {
        super(name, messageFactory, messageLoop.getMessageBus(), listenersHolder, ankorContextFactory,
              remoteMethodActionEventListener);
        this.messageLoop = messageLoop;
    }


    public static SimpleAnkorSystem create(String name, Class<?> modelType, BeanResolver beanResolver) {
        MessageFactory messageFactory = new MessageFactory();

        JsonMessageLoop messageLoop = new JsonMessageLoop(name);

        UnsynchronizedListenersHolder globalEventBus = new UnsynchronizedListenersHolder();

        StandardELContext elContext = new StandardELContext();
        if (beanResolver != null) {
            elContext = elContext.withAdditional(new BeanResolverELResolver(beanResolver));
        }

        Config config = ConfigFactory.load();

        AnkorContextFactory ankorContextFactory
                = new SingletonInstanceAnkorContextFactory(modelType, globalEventBus, elContext, config,
                                                           messageLoop.getMessageBus());

        return new SimpleAnkorSystem(messageFactory, messageLoop, globalEventBus, ankorContextFactory, name, null);
    }

    public static SimpleAnkorSystem create(String name, Class<?> modelType) {
        return create(name, modelType, null);
    }

    public SimpleAnkorSystem withRemoteMethodActionListenerEnabled() {
        return new SimpleAnkorSystem(getMessageFactory(),
                                     messageLoop,
                                     getGlobalListenersHolder(),
                                     getAnkorContextFactory(),
                                     getName(),
                                     new ELRemoteMethodActionEventListener());
    }

    @SuppressWarnings("UnusedDeclaration")
    public static SimpleAnkorSystem create(String name, Class<?> modelType,
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
        });
    }

    public void connectTo(SimpleAnkorSystem other) {
        if (isStarted()) {
            throw new IllegalStateException("cannot connect a started system");
        }

        BlockingQueue<String> thisToOtherQueue = new LinkedBlockingQueue<String>();
        BlockingQueue<String> otherToThisQueue = new LinkedBlockingQueue<String>();

        this.messageLoop.setSendQueue(thisToOtherQueue);
        this.messageLoop.setReceiveQueue(otherToThisQueue);

        other.messageLoop.setSendQueue(otherToThisQueue);
        other.messageLoop.setReceiveQueue(thisToOtherQueue);

        LOG.info("{} is now connected to {}", this, other);
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
