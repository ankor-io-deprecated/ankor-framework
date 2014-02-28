package at.irian.ankor.akka;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.session.ModelSession;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class ControllerActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ControllerActor.class);

    public static Props props(Config config) {
//        int nrOfInstances = config.getInt("at.irian.ankor.akka.ControllerActor.poolSize");
//        return Props.create(ControllerActor.class).withRouter(new RoundRobinRouter(nrOfInstances));
        // todo   controller must not be pooled, because otherwise the event dispatching order is no longer guaranteed
        return Props.create(ControllerActor.class);
    }

    public static String name() {
        return "ankor_controller";
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        if (msg instanceof  ModelEventMsg) {
            String actorName = getActorNameFor(((ModelEventMsg) msg).getModelSession());
            context().actorSelection("/user/" + actorName).tell(((ModelEventMsg)msg).getModelEvent(), self());
        } else if (msg instanceof RegisterMsg) {
            ModelSession modelSession = ((RegisterMsg) msg).getModelSession();
            String actorName = getActorNameFor(modelSession);
            ActorRef actor = getContext().system().actorOf(ModelSessionActor.props(modelSession), actorName);
            LOG.debug("created {}", actor);
        } else if (msg instanceof UnregisterMsg) {
            String actorName = getActorNameFor(((UnregisterMsg) msg).getModelSession());
            context().actorSelection(actorName).tell(PoisonPill.getInstance(), self());
        } else {
            unhandled(msg);
        }
    }

    private String getActorNameFor(ModelSession modelSession) {
        return ModelSessionActor.name(modelSession);
    }

}
