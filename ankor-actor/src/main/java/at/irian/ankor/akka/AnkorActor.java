package at.irian.ankor.akka;

import akka.actor.UntypedActor;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.dispatch.SimpleEventDispatcher;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class AnkorActor extends UntypedActor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorActor.class);

    private EventDispatcher eventDispatcher;

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof InitMsg) {
            handleInit((InitMsg)msg);
        } else if (msg instanceof ModelEvent) {
            handleEvent((ModelEvent) msg);
        } else {
            unhandled(msg);
        }
    }


    private void handleInit(InitMsg msg) {
        this.eventDispatcher = new SimpleEventDispatcher(msg.getSession().getModelContext().getEventListeners());
    }

    private void handleEvent(ModelEvent event) {
        eventDispatcher.dispatch(event);
    }

}
