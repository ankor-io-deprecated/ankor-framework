package at.irian.ankor.switching.connector;

import at.irian.ankor.system.AnkorSystem;

/**
 * A Connector connects an internal or external model instance to
 * the {@link at.irian.ankor.switching.Switchboard Switchboard}.
 * Connectors are pluggable and get loaded by the {@link ConnectorLoader}.
 *
 * @author Manfred Geiler
 */
public interface Connector {

    /**
     * Initialize this Connector.
     */
    void init(AnkorSystem ankorSystem);

    /**
     * Start handling connect and transmit requests.
     */
    void start();

    /**
     * Stop handling connect and transmit requests.
     */
    void stop();

}
