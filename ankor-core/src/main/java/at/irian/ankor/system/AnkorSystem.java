package at.irian.ankor.system;

import at.irian.ankor.application.Application;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.monitor.Monitor;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.ModelSessionFactory;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.SwitchboardImplementor;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.ConnectorLoader;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import com.typesafe.config.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the main system object that sticks all the Ankor parts together.
 * Typically every node in an Ankor environment has exactly one AnkorSystem instance.
 * So, in a pure-Java client-server environment (e.g. JavaFX client and Java web server) there is
 * one AnkorSystem instance on the client side and one AnkorSystem instance on the server side.
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final Application application;
    private final Config config;
    private final SwitchboardImplementor switchboard;
    private final RefContextFactory refContextFactory;
    private final ModelSessionManager modelSessionManager;
    private final ModelSessionFactory modelSessionFactory;
    private final Modifier modifier;
    private final ConnectorLoader connectorLoader;
    private final BeanMetadataProvider beanMetadataProvider;
    private final Scheduler scheduler;
    private final Map<String, Object> attributes;
    private final Monitor monitor;
    private boolean running;

    protected AnkorSystem(Application application,
                          Config config,
                          SwitchboardImplementor switchboard,
                          RefContextFactory refContextFactory,
                          ModelSessionManager modelSessionManager,
                          ModelSessionFactory modelSessionFactory,
                          Modifier modifier,
                          BeanMetadataProvider beanMetadataProvider,
                          Scheduler scheduler, Monitor monitor) {
        this.application = application;
        this.config = config;
        this.switchboard = switchboard;
        this.refContextFactory = refContextFactory;
        this.modelSessionManager = modelSessionManager;
        this.modelSessionFactory = modelSessionFactory;
        this.modifier = modifier;
        this.beanMetadataProvider = beanMetadataProvider;
        this.scheduler = scheduler;
        this.monitor = monitor;
        this.connectorLoader = new ConnectorLoader();
        this.running = false;
        this.attributes = new ConcurrentHashMap<String, Object>();
    }

    public String getSystemName() {
        return application.getName();
    }

    public Config getConfig() {
        return config;
    }

    public Switchboard getSwitchboard() {
        return switchboard;
    }

    public ConnectorRegistry getConnectorPlug() {
        return switchboard.getConnectorRegistry();
    }

    public RefContextFactory getRefContextFactory() {
        return refContextFactory;
    }

    public ModelSessionManager getModelSessionManager() {
        return modelSessionManager;
    }

    public ModelSessionFactory getModelSessionFactory() {
        return modelSessionFactory;
    }

    public Application getApplication() {
        return application;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public BeanMetadataProvider getBeanMetadataProvider() {
        return beanMetadataProvider;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public String toString() {
        return "AnkorSystem{'" + getSystemName() + "'}";
    }

    public AnkorSystem start() {
        LOG.info("Starting {}", this);
        scheduler.init();
        connectorLoader.loadAndInitConnectors(this);
        connectorLoader.startAllConnectors();
        switchboard.start();
        running = true;
        return this;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {
        LOG.info("Stopping {}", this);
        switchboard.stop();
        connectorLoader.stopAllConnectors();
        scheduler.close();
        running = false;
    }

    public void shutdown() {
        if (running) {
            stop();
        }
        modelSessionManager.close();
        application.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        connectorLoader.unloadConnectors();
        super.finalize();
    }

}
