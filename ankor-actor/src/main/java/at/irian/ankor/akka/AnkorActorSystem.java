package at.irian.ankor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.context.ModelContext;
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

    public void register(ModelContext modelContext) {
        controllerActor.tell(new RegisterMsg(modelContext), ActorRef.noSender());
    }

    public void unregister(ModelContext modelContext) {
        controllerActor.tell(new UnregisterMsg(modelContext), ActorRef.noSender());
    }

    public void send(ModelContext context, final ModelEvent event) {
        controllerActor.tell(new ModelEventMsg(context, event), ActorRef.noSender());
    }

    public void close() {
        actorSystem.shutdown();
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
