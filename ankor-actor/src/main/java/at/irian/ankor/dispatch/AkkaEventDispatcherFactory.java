package at.irian.ankor.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.session.Session;

import static at.irian.ankor.akka.AnkorActor.name;
import static at.irian.ankor.akka.AnkorActor.props;

/**
 * @author Manfred Geiler
 */
public class AkkaEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaEventDispatcherFactory.class);

    private final ActorSystem actorSystem;

    public AkkaEventDispatcherFactory(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public EventDispatcher createFor(Session session) {
        final ActorRef actor = actorSystem.actorOf(props(session), name(session));
        return new AkkaEventDispatcher(actorSystem, actor);
    }

}
