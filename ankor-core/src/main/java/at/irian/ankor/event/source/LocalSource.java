package at.irian.ankor.event.source;

import at.irian.ankor.session.ModelSession;

/**
 * Local source of an event.
 *
 * @author Manfred Geiler
 */
public class LocalSource implements Source {

    private final ModelSession modelSession;

    public LocalSource(ModelSession modelSession) {
        this.modelSession = modelSession;
    }

    public ModelSession getModelSession() {
        return modelSession;
    }
}
