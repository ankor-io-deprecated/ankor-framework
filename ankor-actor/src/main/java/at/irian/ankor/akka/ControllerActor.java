package at.irian.ankor.akka;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import at.irian.ankor.context.ModelContext;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class ControllerActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ControllerActor.class);

    public static Props props(Config config) {
        int nrOfInstances = config.getInt("at.irian.ankor.akka.ControllerActor.poolSize");
        return Props.create(ControllerActor.class).withRouter(new RoundRobinRouter(nrOfInstances));
    }

    public static String name() {
        return "ankor_controller";
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        if (msg instanceof  ModelEventMsg) {
            String actorName = getActorNameFor(((ModelEventMsg) msg).getModelContext());
            context().actorSelection("/user/" + actorName).tell(((ModelEventMsg)msg).getModelEvent(), self());
        } else if (msg instanceof RegisterMsg) {
            ModelContext modelContext = ((RegisterMsg) msg).getModelContext();
            String actorName = getActorNameFor(modelContext);
            ActorRef actor = getContext().system().actorOf(ModelContextActor.props(modelContext), actorName);
            LOG.debug("created {}", actor);
        } else if (msg instanceof UnregisterMsg) {
            String actorName = getActorNameFor(((UnregisterMsg) msg).getModelContext());
            context().actorSelection(actorName).tell(PoisonPill.getInstance(), self());
        } else {
            unhandled(msg);
        }
    }

    private String getActorNameFor(ModelContext modelContext) {
        return ModelContextActor.name(modelContext);
    }

}
