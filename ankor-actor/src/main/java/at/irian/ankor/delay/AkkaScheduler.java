package at.irian.ankor.delay;

import akka.actor.ActorSystem;
import at.irian.ankor.akka.AnkorActorSystem;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
public class AkkaScheduler implements Scheduler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaScheduler.class);

    private final ActorSystem actorSystem;

    public AkkaScheduler(AnkorActorSystem ankorActorSystem) {
        this(ankorActorSystem.getActorSystem());
    }

    public AkkaScheduler(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public void schedule(long delayMillis, Runnable runnable) {
        actorSystem.scheduler().scheduleOnce(Duration.create(delayMillis, TimeUnit.MILLISECONDS),
                                             runnable,
                                             actorSystem.dispatcher());
    }
}
