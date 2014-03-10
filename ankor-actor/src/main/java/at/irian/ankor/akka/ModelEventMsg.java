package at.irian.ankor.akka;

import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class ModelEventMsg implements ConsistentHashingRouter.ConsistentHashable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEventMsg.class);

    private final ModelSession modelSession;
    private final ModelEvent modelEvent;

    public ModelEventMsg(ModelSession modelSession, ModelEvent modelEvent) {
        this.modelSession = modelSession;
        this.modelEvent = modelEvent;
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

    public ModelEvent getModelEvent() {
        return modelEvent;
    }

    @Override
    public Object consistentHashKey() {
        return modelSession.getId();
    }
}
