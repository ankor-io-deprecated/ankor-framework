package at.irian.ankor.event.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class AkkaEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaEventDispatcherFactory.class);

    private final ActorRef eventDispatcherActor;

    public AkkaEventDispatcherFactory(ActorSystem actorSystem) {
        this.eventDispatcherActor = actorSystem.actorOf(EventDispatcherActor.props(actorSystem.settings().config()),
                                                        EventDispatcherActor.name());
    }

    @Override
    public EventDispatcher createFor(final ModelSession modelSession) {
        return new EventDispatcher() {
            @Override
            public void dispatch(ModelEvent event) {
                eventDispatcherActor.tell(new EventDispatcherActor.ModelEventMsg(modelSession, event), ActorRef.noSender());
            }

            @Override
            public void close() {

            }
        };
    }

}
