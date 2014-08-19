package at.irian.ankor.event.dispatch;

import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.event.Event;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class AkkaModelEventMsg implements ConsistentHashingRouter.ConsistentHashable {
    private final ModelSession modelSession;
    private final Event event;

    public AkkaModelEventMsg(ModelSession modelSession, Event event) {
        this.modelSession = modelSession;
        this.event = event;
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

    public Event getModelEvent() {
        return event;
    }

    @Override
    public Object consistentHashKey() {
        return modelSession.getId();
    }

    @Override
    public String toString() {
        return "AkkaModelEventMsg{" +
               "modelSession=" + modelSession +
               ", event=" + event +
               '}';
    }
}
