package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.ModelSession;

/**
* @author Manfred Geiler
*/
class AkkaEventDispatcher implements EventDispatcher {

    private final ModelSession modelSession;
    private final ActorRef eventDispatcherActor;

    public AkkaEventDispatcher(ModelSession modelSession, ActorRef eventDispatcherActor) {
        this.modelSession = modelSession;
        this.eventDispatcherActor = eventDispatcherActor;
    }

    @Override
    public void dispatch(ModelEvent event) {
        eventDispatcherActor.tell(new EventDispatcherActor.ModelEventMsg(modelSession, event), ActorRef.noSender());
    }

    @Override
    public void close() {
    }
}
