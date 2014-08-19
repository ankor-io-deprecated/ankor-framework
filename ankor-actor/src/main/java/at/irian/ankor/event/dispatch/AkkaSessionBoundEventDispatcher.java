package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import at.irian.ankor.event.Event;
import at.irian.ankor.session.ModelSession;

/**
* @author Manfred Geiler
*/
class AkkaSessionBoundEventDispatcher implements EventDispatcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaSessionBoundEventDispatcher.class);

    private final ModelSession modelSession;
    private final ActorRef eventDispatcherActor;

    public AkkaSessionBoundEventDispatcher(ModelSession modelSession, ActorRef eventDispatcherActor) {
        this.modelSession = modelSession;
        this.eventDispatcherActor = eventDispatcherActor;
    }

    @Override
    public void dispatch(Event event) {
        eventDispatcherActor.tell(new AkkaModelEventMsg(modelSession, event), ActorRef.noSender());
    }

    @Override
    public void close() {
        LOG.info("Killing eventDispatcherActor {}", eventDispatcherActor.path().name());
        eventDispatcherActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
