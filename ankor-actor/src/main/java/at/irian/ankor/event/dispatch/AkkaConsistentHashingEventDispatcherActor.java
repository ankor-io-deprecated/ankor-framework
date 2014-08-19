package at.irian.ankor.event.dispatch;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.event.Event;
import at.irian.ankor.session.ModelSession;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class AkkaConsistentHashingEventDispatcherActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaConsistentHashingEventDispatcherActor.class);

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        int nrOfInstances = config.getInt("at.irian.ankor.event.dispatch.AkkaConsistentHashingEventDispatcherActor.poolSize");
        return Props.create(AkkaConsistentHashingEventDispatcherActor.class)
                    .withDispatcher("ankor.event-dispatcher")
                    .withRouter(new ConsistentHashingRouter(nrOfInstances));
    }

    public static String name() {
        return "ankor_event_dispatcher";
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        if (msg instanceof AkkaModelEventMsg) {
            handleEvent(((AkkaModelEventMsg) msg).getModelSession(), ((AkkaModelEventMsg) msg).getModelEvent());
        } else {
            unhandled(msg);
        }
    }

    private void handleEvent(ModelSession modelSession, Event event) {
        DispatchThreadChecker dispatchThreadChecker = new DispatchThreadChecker(modelSession);
        boolean registered = dispatchThreadChecker.registerCurrentThread();

        EventDispatcher eventDispatcher = new SimpleEventDispatcher(modelSession.getEventListeners());
        try {
            eventDispatcher.dispatch(event);
        } finally {
            if (registered) {
                dispatchThreadChecker.clear();
            }
        }
    }

}
