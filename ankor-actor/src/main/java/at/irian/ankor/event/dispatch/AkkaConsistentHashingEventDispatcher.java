package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import at.irian.ankor.event.Event;
import at.irian.ankor.session.ModelSession;

/**
* @author Manfred Geiler
*/
class AkkaConsistentHashingEventDispatcher implements EventDispatcher {

    private final ModelSession modelSession;
    private final ActorRef eventDispatcherActor;

    public AkkaConsistentHashingEventDispatcher(ModelSession modelSession, ActorRef eventDispatcherActor) {
        this.modelSession = modelSession;
        this.eventDispatcherActor = eventDispatcherActor;
    }

    @Override
    public void dispatch(Event event) {
        eventDispatcherActor.tell(new AkkaModelEventMsg(modelSession, event), ActorRef.noSender());
    }

    @Override
    public void close() {
    }
}
