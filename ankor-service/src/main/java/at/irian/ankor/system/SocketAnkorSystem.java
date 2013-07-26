package at.irian.ankor.system;

import at.irian.ankor.annotation.BeanAnnotationActionEventListener;
import at.irian.ankor.annotation.BeanAnnotationChangeEventListener;
import at.irian.ankor.annotation.ViewModelAnnotationScanner;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.*;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.model.ViewModelPropertyFieldsInitializer;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.SingletonModelELRefContextFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class SocketAnkorSystem extends AnkorSystem {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketAnkorSystem.class);

    private final MessageLoop<String> messageLoop;
    private final BeanResolver beanResolver;

    protected SocketAnkorSystem(MessageFactory messageFactory,
                                MessageLoop<String> messageLoop,
                                RefContextFactory refContextFactory,
                                EventListeners globalEventListeners,
                                String name, BeanResolver beanResolver) {
        super(name, messageFactory, messageLoop.getMessageBus(), globalEventListeners, refContextFactory);
        this.messageLoop = messageLoop;
        this.beanResolver = beanResolver;
    }


    public static SocketAnkorSystem create(String systemName, Class<?> modelType, BeanResolver beanResolver,
                                           String remoteHost, int remotePort, int localPort,
                                           MessageMapper<String> messageMapper) {
        MessageFactory messageFactory = new MessageFactory();

        MessageLoop<String> messageLoop = new SocketMessageLoop<String>(systemName, messageMapper,
                remoteHost, remotePort, localPort);

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

        return new SocketAnkorSystem(messageFactory, messageLoop, refContextFactory, globalEventListeners, systemName, beanResolver);
    }

    @Override
    public void start() {
        if (!messageLoop.isConnected()) {
            throw new IllegalStateException("message loop is not connected");
        }

        super.start();

        if (beanResolver != null) {
            PathSyntax pathSyntax = getRefContextFactory().getPathSyntax();
            getGlobalEventListeners().add(new BeanAnnotationActionEventListener(beanResolver));
            getGlobalEventListeners().add(new BeanAnnotationChangeEventListener(beanResolver, pathSyntax));
            // todo  cleanup in stop()
        }

        messageLoop.start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoopbackMessageBus<String> getMessageBus() {
        return (LoopbackMessageBus<String>) super.getMessageBus();
    }

}
