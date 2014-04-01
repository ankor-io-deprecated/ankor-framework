package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

/**
 * @author Manfred Geiler
 */
public class SocketStatelessStandaloneServer extends SocketStandaloneServer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketStatelessStandaloneServer.class);

    public SocketStatelessStandaloneServer(Application application) {
        super(application);
    }

    @Override
    protected AnkorSystem createAnkorSystem() {
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        return new AnkorSystemBuilder()
                .withApplication(application)
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", true)
                .withStateless(true)
                .createServer();
    }
}
