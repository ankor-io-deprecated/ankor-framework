package at.irian.ankor.event.dispatch;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.worker.WorkerContext;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class EventDispatcherActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventDispatcherActor.class);

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        int nrOfInstances = config.getInt("at.irian.ankor.event.dispatch.EventDispatcherActor.poolSize");
        return Props.create(EventDispatcherActor.class).withRouter(new ConsistentHashingRouter(nrOfInstances));
    }

    public static String name() {
        return "ankor_event_dispatcher";
    }

    private final WorkerContext workerContext = new WorkerContext();

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        WorkerContext.setCurrentInstance(workerContext);
        try {
            if (msg instanceof ModelEventMsg) {
                handleEvent(((ModelEventMsg) msg).getModelSession(), ((ModelEventMsg) msg).getModelEvent());
            } else {
                unhandled(msg);
            }
        } finally {
            WorkerContext.setCurrentInstance(null);
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

    /**
     * @author Manfred Geiler
     */
    public static class ModelEventMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelSession modelSession;
        private final ModelEvent modelEvent;

        public ModelEventMsg(ModelSession modelSession, ModelEvent modelEvent) {
            this.modelSession = modelSession;
            this.modelEvent = modelEvent;
        }

        public ModelSession getModelSession() {
            return modelSession;
        }

        public ModelEvent getModelEvent() {
            return modelEvent;
        }

        @Override
        public Object consistentHashKey() {
            return modelSession.getId();
        }
    }
}
