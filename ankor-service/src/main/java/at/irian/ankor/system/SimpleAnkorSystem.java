package at.irian.ankor.system;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.LoopbackMessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.PipeMessageLoop;
import at.irian.ankor.messaging.json.JsonMessageMapper;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.SingletonModelELRefContextFactory;
import at.irian.ankor.rmi.ELRemoteMethodActionEventListener;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Manfred Geiler
 */
public class SimpleAnkorSystem extends AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorSystem.class);

    private final PipeMessageLoop<String> messageLoop;

    protected SimpleAnkorSystem(MessageFactory messageFactory,
                                PipeMessageLoop<String> messageLoop,
                                RefContextFactory refContextFactory,
                                EventListeners globalEventListeners,
                                String name,
                                RemoteMethodActionEventListener remoteMethodActionEventListener) {
        super(name, messageFactory, messageLoop.getMessageBus(), globalEventListeners, refContextFactory,
              remoteMethodActionEventListener);
        this.messageLoop = messageLoop;
    }


    public static SimpleAnkorSystem create(String systemName, Class<?> modelType, BeanResolver beanResolver) {
        MessageFactory messageFactory = new MessageFactory();

        PipeMessageLoop<String> messageLoop = new PipeMessageLoop<String>(systemName, new JsonMessageMapper());

        EventListeners globalEventListeners = new ArrayListEventListeners();

        Config config = ConfigFactory.load();

        EventDelaySupport eventDelaySupport = new EventDelaySupport(systemName);

        RefContextFactory refContextFactory = SingletonModelELRefContextFactory.getInstance(config,
                                                                                            modelType,
                                                                                            globalEventListeners,
                                                                                            messageLoop.getMessageBus(),
                                                                                            beanResolver,
                                                                                            eventDelaySupport);

        return new SimpleAnkorSystem(messageFactory, messageLoop, refContextFactory, globalEventListeners,
                                     systemName, null);
    }

    public static SimpleAnkorSystem create(String name, Class<?> modelType) {
        return create(name, modelType, null);
    }

    public SimpleAnkorSystem withRemoteMethodActionListenerEnabled() {
        return new SimpleAnkorSystem(getMessageFactory(),
                                     messageLoop,
                                     getRefContextFactory(),
                                     getGlobalEventListeners(),
                                     getSystemName(),
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

            @Override
            public String[] getBeanDefinitionNames() {
                return new String[] {singletonBeanName};
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
