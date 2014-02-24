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
    private final ActorRef controllerActor;

    private AnkorActorSystem(ActorSystem actorSystem, ActorRef controllerActor) {
        this.actorSystem = actorSystem;
        this.controllerActor = controllerActor;
    }

    public static AnkorActorSystem create() {
        LOG.info("Starting akka actor system");
        ActorSystem actorSystem = ActorSystem.create("ankor");
        return createFor(actorSystem);
    }

    public static AnkorActorSystem createFor(ActorSystem actorSystem) {
        ActorRef controllerActor = actorSystem.actorOf(ControllerActor.props(actorSystem.settings().config()),
                                                       ControllerActor.name());
        return new AnkorActorSystem(actorSystem, controllerActor);
    }

    public void register(ModelSession modelSession) {
        controllerActor.tell(new RegisterMsg(modelSession), ActorRef.noSender());
    }

    public void unregister(ModelSession modelSession) {
        controllerActor.tell(new UnregisterMsg(modelSession), ActorRef.noSender());
    }

    public void send(ModelSession context, final ModelEvent event) {
        controllerActor.tell(new ModelEventMsg(context, event), ActorRef.noSender());
    }

    public void close() {
        actorSystem.shutdown();
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
