package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.event.ModelEvent;

/**
* @author Manfred Geiler
*/
class AkkaEventDispatcher implements EventDispatcher {

    private final ActorSystem actorSystem;
    private final ActorRef actor;

    public AkkaEventDispatcher(ActorSystem actorSystem, ActorRef actor) {
        this.actorSystem = actorSystem;
        this.actor = actor;
    }

    @Override
    public void dispatch(ModelEvent event) {
       actor.tell(event, null);
    }

    @Override
    public void close() {
        actorSystem.stop(actor);
    }
}
