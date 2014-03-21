package at.irian.ankor.event.dispatch;

import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class AkkaModelEventMsg implements ConsistentHashingRouter.ConsistentHashable {
    private final ModelSession modelSession;
    private final ModelEvent modelEvent;

    public AkkaModelEventMsg(ModelSession modelSession, ModelEvent modelEvent) {
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
