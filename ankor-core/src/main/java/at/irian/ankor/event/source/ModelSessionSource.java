package at.irian.ankor.event.source;

import at.irian.ankor.connector.local.LocalModelSessionParty;
import at.irian.ankor.session.ModelSession;

/**
 * Local source of an event.
 *
 * @author Manfred Geiler
 */
public class ModelSessionSource extends PartySource {

    private final ModelSession modelSession;

    public ModelSessionSource(ModelSession modelSession) {
        super(new LocalModelSessionParty(modelSession.getId()));
        this.modelSession = modelSession;
    }

    public ModelSession getModelSession() {
        return modelSession;
    }
}
