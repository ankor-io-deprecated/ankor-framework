package at.irian.ankor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.dispatch.EventDispatcherFactory;
import at.irian.ankor.session.Session;

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

        final ActorRef actor = actorSystem.actorOf(new Props(AnkorActor.class), "ankor_" + session.getId());
        actor.tell(new InitMsg(session));

        return new AkkaEventDispatcher(actor);
    }

}
