package at.irian.ankor.connector;

import at.irian.ankor.system.AnkorSystem;

/**
 * @author Manfred Geiler
 */
public interface Connector {

    void init(AnkorSystem ankorSystem);
    void start();
    void stop();

}
