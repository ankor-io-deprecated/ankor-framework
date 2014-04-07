package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

/**
 * @author Manfred Geiler
 */
public class SocketStandaloneServer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketStandaloneServer.class);

    protected final Application application;
    private Thread thread;
    private volatile boolean running;

    public SocketStandaloneServer(Application application) {
        this.application = application;
    }

    public void start() {
        thread = createMainThread();
        running = true;
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
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        return new AnkorSystemBuilder()
                .withApplication(application)
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", true)
                .createServer();
    }

    protected void sleepForever() {
        boolean interrupted = false;
        while (running && !interrupted) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        Thread.currentThread().interrupt();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }
}
