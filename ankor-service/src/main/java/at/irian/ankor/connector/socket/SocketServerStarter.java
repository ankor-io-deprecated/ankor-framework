package at.irian.ankor.connector.socket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

/**
 * @author Manfred Geiler
 */
public class SocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketServerStarter.class);

    private final Application application;

    public SocketServerStarter(Application application) {
        this.application = application;
    }

    public void start() {
        Thread thread = createMainThread();
        thread.start();
        sleepForever();
    }

    protected Thread createMainThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createAnkorSystem()
                        .start();
            }
        });
        thread.setDaemon(false);
        thread.setName(application.getName() + " main server thread");
        return thread;
    }

    protected AnkorSystem createAnkorSystem() {
        AnkorActorSystem ankorActorSystem = AnkorActorSystem.create();
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        return new AnkorSystemBuilder()
                .withApplication(application)
                .withDispatcherFactory(new AkkaEventDispatcherFactory(ankorActorSystem))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.connector.socket.SocketConnector.enabled", true)
                .createServer();
    }

    protected void sleepForever() {
        boolean interrupted = false;
        while (!interrupted) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        Thread.currentThread().interrupt();
    }
}
