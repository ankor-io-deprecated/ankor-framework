package at.irian.ankor.servlet.polling;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Manfred Geiler
 */
public abstract class AnkorServletContextListener implements ServletContextListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServletContextListener.class);

    private static final String ANKOR_SYSTEM_ATTR = AnkorServletContextListener.class + ".ANKOR_SYSTEM";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        AnkorSystem ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
                .withBeanResolver(getBeanResolver())
                .withApplication(getApplication())
                //.withMessageBus(new ServletMessageBus(new ViewModelJsonMessageMapper(beanMetadataProvider)))
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .createServer();

        // todo  register ServletEventMessageListener
        // todo  forward received servlet messages to MessageBus

        sce.getServletContext().setAttribute(ANKOR_SYSTEM_ATTR, ankorSystem);

        ankorSystem.start();
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(ANKOR_SYSTEM_ATTR);
    }

    static AnkorSystem getAnkorSystem(ServletContext servletContext) {
        return (AnkorSystem) servletContext.getAttribute(ANKOR_SYSTEM_ATTR);
    }

    protected abstract String getName();
    protected abstract BeanResolver getBeanResolver();
    protected abstract Application getApplication();

}
