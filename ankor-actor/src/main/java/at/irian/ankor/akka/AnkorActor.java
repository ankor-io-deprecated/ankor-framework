package at.irian.ankor.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.dispatch.SimpleEventDispatcher;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class AnkorActor extends UntypedActor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorActor.class);

    public static Props props(Session session) {
        return Props.create(AnkorActor.class, session);
    }

    public static String name(Session session) {
        return "ankor_" + session.getId();
    }

    private final EventDispatcher eventDispatcher;

    public AnkorActor(Session session) {
        ModelContext modelContext = session.getModelContext();
        this.eventDispatcher = new SimpleEventDispatcher(modelContext.getEventListeners());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof ModelEvent) {
            handleEvent((ModelEvent) msg);
        } else {
            unhandled(msg);
        }
    }

    private void handleEvent(ModelEvent event) {
        eventDispatcher.dispatch(event);
    }

}
