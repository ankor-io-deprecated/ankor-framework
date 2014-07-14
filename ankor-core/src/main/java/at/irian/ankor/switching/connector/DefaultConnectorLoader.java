package at.irian.ankor.switching.connector;

import at.irian.ankor.system.AnkorSystem;

import java.util.ServiceLoader;

/**
 * Default implementation of the {@link at.irian.ankor.switching.connector.ConnectorLoader ConnectorLoader} which
 * uses the Java standard ServiceLoader pattern to locate and load
 * pluggable {@link at.irian.ankor.switching.connector.Connector connectors}.
 *
 * @author Manfred Geiler
 */
public class DefaultConnectorLoader implements ConnectorLoader {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectorLoader.class);

    private ServiceLoader<Connector> serviceLoader = null;

    @Override
    public void loadAndInitConnectors(AnkorSystem ankorSystem) {
        this.serviceLoader = ServiceLoader.load(Connector.class);
        for (Connector c : serviceLoader) {
            c.init(ankorSystem);
        }
    }

    @Override
    public void unloadConnectors() {
        this.serviceLoader = null;
    }

    @Override
    public void startAllConnectors() {
        if (serviceLoader == null) {
            throw new IllegalStateException("Connectors not yet loaded");
        }
        for (Connector c : serviceLoader) {
            c.start();
        }
    }

    @Override
    public void stopAllConnectors() {
        if (serviceLoader == null) {
            throw new IllegalStateException("Connectors not yet loaded");
        }
        for (Connector c : serviceLoader) {
            c.stop();
        }
    }
}
