package at.irian.ankor.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.dispatch.DispatchThreadChecker;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.SimpleEventDispatcher;

/**
 * @author Manfred Geiler
 */
public class ModelContextActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContextActor.class);

    public static Props props(ModelContext modelContext) {
        return Props.create(ModelContextActor.class, modelContext);
    }

    public static String name(ModelContext modelContext) {
        return "ankor_" + modelContext.getId();
    }

    private final EventDispatcher eventDispatcher;
    private final DispatchThreadChecker dispatchThreadChecker;

    public ModelContextActor(ModelContext modelContext) {
        this.eventDispatcher = new SimpleEventDispatcher(modelContext.getEventListeners());
        this.dispatchThreadChecker = new DispatchThreadChecker(modelContext);
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
