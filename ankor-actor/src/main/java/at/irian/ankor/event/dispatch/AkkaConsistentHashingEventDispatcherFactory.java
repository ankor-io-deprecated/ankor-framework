package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class AkkaConsistentHashingEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaConsistentHashingEventDispatcherFactory.class);

    private final ActorRef eventDispatcherActor;

    public AkkaConsistentHashingEventDispatcherFactory(ActorSystem actorSystem) {
        this.eventDispatcherActor = actorSystem.actorOf(AkkaConsistentHashingEventDispatcherActor.props(actorSystem.settings()
                                                                                                                   .config()),
                                                        AkkaConsistentHashingEventDispatcherActor.name());
    }

    @Override
    public EventDispatcher createFor(final ModelSession modelSession) {
        return new AkkaConsistentHashingEventDispatcher(modelSession, eventDispatcherActor);
    }

}
