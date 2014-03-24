package at.irian.ankor.event.dispatch;

import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.ModelSession;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class AkkaSessionBoundEventDispatcherActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaSessionBoundEventDispatcherActor.class);

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        return Props.create(AkkaSessionBoundEventDispatcherActor.class)
                    .withDispatcher("at.irian.ankor.event.dispatch.ankor-event-dispatcher");
    }

    public static String name(ModelSession modelSession) {
        return "ankor_event_dispatcher_" + modelSession.getId();
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

    private void handleEvent(ModelSession modelSession, ModelEvent event) {
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
