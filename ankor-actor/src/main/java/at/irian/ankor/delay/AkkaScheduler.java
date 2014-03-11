package at.irian.ankor.delay;

import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
public class AkkaScheduler implements Scheduler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaScheduler.class);

    private final ActorSystem actorSystem;

    public AkkaScheduler(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public Cancellable schedule(long delayMillis, Runnable runnable) {
        final akka.actor.Cancellable akkaCancellable = actorSystem.scheduler()
                                             .scheduleOnce(Duration.create(delayMillis, TimeUnit.MILLISECONDS),
                                                           runnable,
                                                           actorSystem.dispatcher());
        return new Cancellable() {
            @Override
            public void cancel() {
                akkaCancellable.cancel();
            }
        };
    }
}
