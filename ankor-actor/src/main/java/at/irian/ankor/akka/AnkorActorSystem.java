package at.irian.ankor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class AnkorActorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorActorSystem.class);

    private final ActorSystem actorSystem;
    private final ActorRef eventDispatcherActor;

    private AnkorActorSystem(ActorSystem actorSystem, ActorRef eventDispatcherActor) {
        this.actorSystem = actorSystem;
        this.eventDispatcherActor = eventDispatcherActor;
    }

    public static AnkorActorSystem create() {
        LOG.info("Starting akka actor system");
        ActorSystem actorSystem = ActorSystem.create("ankor");
        return createFor(actorSystem);
    }

    public static AnkorActorSystem createFor(ActorSystem actorSystem) {
        ActorRef eventDispatcherActor = actorSystem.actorOf(EventDispatcherActor.props(actorSystem.settings().config()),
                                                            EventDispatcherActor.name());
        return new AnkorActorSystem(actorSystem, eventDispatcherActor);
    }

    public void send(ModelSession context, final ModelEvent event) {
        eventDispatcherActor.tell(new ModelEventMsg(context, event), ActorRef.noSender());
    }

    public void close() {
        actorSystem.shutdown();
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
