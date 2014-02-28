package at.irian.ankor.event.dispatch;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class AkkaEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaEventDispatcherFactory.class);

    private final AnkorActorSystem ankorActorSystem;

    public AkkaEventDispatcherFactory() {
        this.ankorActorSystem = AnkorActorSystem.create();
    }

    public AkkaEventDispatcherFactory(AnkorActorSystem ankorActorSystem) {
        this.ankorActorSystem = ankorActorSystem;
    }

    @Override
    public EventDispatcher createFor(final ModelSession modelSession) {

        ankorActorSystem.register(modelSession);

        return new EventDispatcher() {
            @Override
            public void dispatch(ModelEvent event) {
                ankorActorSystem.send(modelSession, event);
            }

            @Override
            public void close() {
                ankorActorSystem.unregister(modelSession);
            }
        };
    }

    @Override
    public void close() {
        ankorActorSystem.close();
    }

}
