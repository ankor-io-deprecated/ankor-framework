package at.irian.ankor.monitor;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public interface ModelSessionMonitor {

    void monitor_create(ModelSession modelSession);
    void monitor_close(ModelSession modelSession);

}
