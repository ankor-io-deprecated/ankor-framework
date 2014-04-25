package at.irian.ankor.switching.connector.local;

import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.system.AnkorSystem;

/**
 * Pluggable {@link at.irian.ankor.switching.connector.Connector connector} with handlers for connecting to a
 * local model instance and for transmitting event messages to local model instances.
 * This {@link at.irian.ankor.switching.connector.Connector Connector} supports both stateful models and stateless models.
 *
 * @author Manfred Geiler
 */
public class LocalConnector implements Connector {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnector.class);

    private AnkorSystem system;

    @Override
    public void init(AnkorSystem system) {
        this.system = system;
    }

    @Override
    public void start() {
        ConnectorRegistry plug = system.getConnectorPlug();

        plug.registerTransmissionHandler(StatefulSessionModelAddress.class,
                                         new StatefulSessionTransmissionHandler(system.getModelSessionManager(),
                                                                                system.getModifier()));
        plug.registerConnectionHandler(StatefulSessionModelAddress.class,
                                       new StatefulSessionConnectionHandler(system.getModelSessionManager(),
                                                                            system.getSwitchboard()));

        plug.registerTransmissionHandler(StatelessSessionModelAddress.class,
                                         new StatelessSessionTransmissionHandler(system.getModelSessionFactory(),
                                                                                 system.getApplication(),
                                                                                 system.getModifier()));
        plug.registerConnectionHandler(StatelessSessionModelAddress.class,
                                       new StatelessSessionConnectionHandler(system.getModelSessionFactory(),
                                                                             system.getApplication(),
                                                                             system.getSwitchboard()));
    }

    @Override
    public void stop() {
        ConnectorRegistry plug = system.getConnectorPlug();

        plug.unregisterTransmissionHandler(StatefulSessionModelAddress.class);
        plug.unregisterConnectionHandler(StatefulSessionModelAddress.class);

        plug.unregisterTransmissionHandler(StatelessSessionModelAddress.class);
        plug.unregisterConnectionHandler(StatelessSessionModelAddress.class);
    }

}
