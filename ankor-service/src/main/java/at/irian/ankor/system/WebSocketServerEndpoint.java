package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.switching.connector.websocket.WebSocketEndpoint;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public abstract class WebSocketServerEndpoint extends WebSocketEndpoint {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketServerEndpoint.class);

    private static final Map<Class<? extends WebSocketServerEndpoint>, AnkorSystem> SYSTEM_MAP
            = new ConcurrentHashMap<Class<? extends WebSocketServerEndpoint>, AnkorSystem>();

    @Override
    protected AnkorSystem getAnkorSystem() {
        AnkorSystem ankorSystem = SYSTEM_MAP.get(this.getClass());
        if (ankorSystem == null) {
            synchronized (SYSTEM_MAP) {
                ankorSystem = SYSTEM_MAP.get(this.getClass());
                if (ankorSystem == null) {
                    ankorSystem = createAnkorSystem();
                    SYSTEM_MAP.put(this.getClass(), ankorSystem);
                }
            }
        }
        return ankorSystem;
    }

    protected AnkorSystem createAnkorSystem() {
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        return new AnkorSystemBuilder()
                .withApplication(createApplication())
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled", true)
                .createServer();
    }

    protected abstract Application createApplication();

}
