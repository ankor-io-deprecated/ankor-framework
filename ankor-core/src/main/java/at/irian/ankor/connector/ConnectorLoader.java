package at.irian.ankor.connector;

import at.irian.ankor.system.AnkorSystem;

import java.util.ServiceLoader;

/**
 * @author Manfred Geiler
 */
public class ConnectorLoader {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectorLoader.class);

    private ServiceLoader<Connector> serviceLoader = null;

    public void loadAndInitConnectors(AnkorSystem ankorSystem) {
        this.serviceLoader = ServiceLoader.load(Connector.class);
        for (Connector c : serviceLoader) {
            c.init(ankorSystem);
        }
    }

    public void unloadConnectors() {
        this.serviceLoader = null;
    }

    public void startAllConnectors() {
        if (serviceLoader == null) {
            throw new IllegalStateException("Connectors not yet loaded");
        }
        for (Connector c : serviceLoader) {
            c.start();
        }
    }

    public void stopAllConnectors() {
        if (serviceLoader == null) {
            throw new IllegalStateException("Connectors not yet loaded");
        }
        for (Connector c : serviceLoader) {
            c.stop();
        }
    }
}
