package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.annotation.BeanAnnotationActionEventListener;
import at.irian.ankor.annotation.BeanAnnotationChangeEventListener;
import at.irian.ankor.annotation.ViewModelAnnotationScanner;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.LoopbackMessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.PipeMessageLoop;
import at.irian.ankor.messaging.json.JsonViewModelMessageMapper;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.model.ViewModelPropertyFieldsInitializer;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.SingletonModelELRefContextFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;
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
                                ActionEvent.Listener annotationActionEventListener,
                                ChangeEventListener annotationChangeEventListener) {
        super(name, messageFactory, messageLoop.getMessageBus(), globalEventListeners, refContextFactory,
              annotationActionEventListener, annotationChangeEventListener);
        this.messageLoop = messageLoop;
    }


    public static SimpleAnkorSystem create(String systemName,
                                           Class<?> modelType,
                                           BeanResolver beanResolver,
                                           boolean enableAnnotationListeners,
                                           MessageMapper<String> messageMapper) {
        MessageFactory messageFactory = new MessageFactory();

        PipeMessageLoop<String> messageLoop = new PipeMessageLoop<String>(systemName, messageMapper);

        EventListeners globalEventListeners = new ArrayListEventListeners();

        Config config = ConfigFactory.load();

        EventDelaySupport eventDelaySupport = new EventDelaySupport(systemName);

        List<ViewModelPostProcessor> viewModelPostProcessors = new ArrayList<ViewModelPostProcessor>();
        viewModelPostProcessors.add(new ViewModelPropertyFieldsInitializer());
        viewModelPostProcessors.add(new ViewModelAnnotationScanner());

        RefContextFactory refContextFactory = SingletonModelELRefContextFactory.getInstance(config,
                                                                                            modelType,
                                                                                            globalEventListeners,
                                                                                            messageLoop.getMessageBus(),
                                                                                            beanResolver,
                                                                                            eventDelaySupport,
                                                                                            viewModelPostProcessors);
        //jsonMessageMapper.init(refContextFactory.);

        ActionEvent.Listener annotationActionEventListener = null;
        ChangeEventListener annotationChangeEventListener = null;
        if (enableAnnotationListeners) {
            annotationActionEventListener = new BeanAnnotationActionEventListener(beanResolver);
            annotationChangeEventListener = new BeanAnnotationChangeEventListener(beanResolver, refContextFactory.createRefContext().pathSyntax());
        }

        return new SimpleAnkorSystem(messageFactory, messageLoop, refContextFactory, globalEventListeners,
                                     systemName, annotationActionEventListener, annotationChangeEventListener);
    }

    public static SimpleAnkorSystem create(String name, Class<?> modelType, boolean enableRemoteActionListener) {
        return create(name, modelType, null, enableRemoteActionListener, new JsonViewModelMessageMapper());
    }

    @SuppressWarnings("UnusedDeclaration")
    public static SimpleAnkorSystem create(String name, Class<?> modelType,
                                     final String singletonBeanName, final Object singletonBean, boolean enableRemoteActionListener) {
        BeanResolver beanResolver = new BeanResolver() {
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
                return new String[]{singletonBeanName};
            }
        };
        return create(name, modelType, beanResolver, enableRemoteActionListener, new JsonViewModelMessageMapper());
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
