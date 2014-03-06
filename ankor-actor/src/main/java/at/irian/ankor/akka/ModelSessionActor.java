package at.irian.ankor.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.dispatch.DispatchThreadChecker;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.SimpleEventDispatcher;

/**
 * @author Manfred Geiler
 */
public class ModelSessionActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSessionActor.class);

    public static Props props(ModelSession modelSession) {
        return Props.create(ModelSessionActor.class, modelSession);
    }

    public static String name(ModelSession modelSession) {
        return "ankor_" + modelSession.getId();
    }

    private final EventDispatcher eventDispatcher;
    private final DispatchThreadChecker dispatchThreadChecker;

    public ModelSessionActor(ModelSession modelSession) {
        this.eventDispatcher = new SimpleEventDispatcher(modelSession.getEventListeners());
        this.dispatchThreadChecker = new DispatchThreadChecker(modelSession);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        if (msg instanceof ModelEvent) {
            handleEvent((ModelEvent) msg);
        } else {
            unhandled(msg);
        }
    }

    private void handleEvent(ModelEvent event) {
        boolean registered = dispatchThreadChecker.registerCurrentThread();
        try {
            eventDispatcher.dispatch(event);
        } finally {
            if (registered) {
                dispatchThreadChecker.clear();
            }
        }
    }

}
