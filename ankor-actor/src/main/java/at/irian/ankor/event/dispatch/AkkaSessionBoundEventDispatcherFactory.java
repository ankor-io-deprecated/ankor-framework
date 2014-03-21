package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class AkkaSessionBoundEventDispatcherFactory implements EventDispatcherFactory {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaSessionBoundEventDispatcherFactory.class);

    private final ActorSystem actorSystem;

    public AkkaSessionBoundEventDispatcherFactory(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public EventDispatcher createFor(final ModelSession modelSession) {
        ActorRef eventDispatcherActor = actorSystem.actorOf(AkkaSessionBoundEventDispatcherActor.props(actorSystem.settings().config()),
                                                            AkkaSessionBoundEventDispatcherActor.name(modelSession));
        LOG.info("Created new eventDispatcherActor {}", eventDispatcherActor.path().name());
        return new AkkaSessionBoundEventDispatcher(modelSession, eventDispatcherActor);
    }

}
