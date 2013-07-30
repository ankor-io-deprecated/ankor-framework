package at.irian.ankor.akka;

import akka.actor.ActorRef;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.event.ModelEvent;

/**
* @author Manfred Geiler
*/
class AkkaEventDispatcher implements EventDispatcher {

    private final ActorRef actor;

    public AkkaEventDispatcher(ActorRef actor) {
        this.actor = actor;
    }

    @Override
    public void dispatch(ModelEvent event) {
       actor.tell(event, null);
    }

}
