package at.irian.ankor.monitor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.monitor.AnkorSystemMonitor;
import at.irian.ankor.monitor.ModelSessionMonitor;
import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.ModelAddressQualifier;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class AkkaAnkorSystemMonitor implements AnkorSystemMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaAnkorSystemMonitor.class);

    private final ActorRef monitorActor;

    public AkkaAnkorSystemMonitor(ActorRef monitorActor) {
        this.monitorActor = monitorActor;
    }

    public static AkkaAnkorSystemMonitor create(ActorSystem actorSystem, AnkorSystemStats stats) {
        Config config = actorSystem.settings().config();
        ActorRef monitorActor = actorSystem.actorOf(MonitorActor.props(config, stats),
                                                    MonitorActor.name());
        return new AkkaAnkorSystemMonitor(monitorActor);
    }

    private final SwitchboardMonitor switchboardMonitor = new SwitchboardMonitor() {
        @Override
        public void monitor_openConnection(final Switchboard sb, final ModelAddress sender, final Map<String, Object> connectParameters) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_openConnection(sb, sender, connectParameters);
                }
            });
        }

        @Override
        public void monitor_send(final Switchboard sb, final ModelAddress sender, final EventMessage message) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_send(sb, sender, message);
                }
            });
        }

        @Override
        public void monitor_send(final Switchboard sb, final ModelAddress sender, final EventMessage message, final ModelAddress receiver) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_send(sb, sender, message, receiver);
                }
            });
        }

        @Override
        public void monitor_closeAllConnections(final Switchboard sb, final ModelAddress sender) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_closeAllConnections(sb, sender);
                }
            });
        }

        @Override
        public void monitor_closeConnection(final Switchboard sb, final ModelAddress sender, final ModelAddress receiver) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_closeConnection(sb, sender, receiver);
                }
            });
        }

        @Override
        public void monitor_closeQualifyingConnections(final Switchboard sb, final ModelAddressQualifier qualifier) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_closeQualifyingConnections(sb, qualifier);
                }
            });
        }

        @Override
        public void monitor_start(final Switchboard sb) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_start(sb);
                }
            });
        }

        @Override
        public void monitor_stop(final Switchboard sb) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.switchboard().monitor_stop(sb);
                }
            });
        }
    };

    private final ModelSessionMonitor modelSessionMonitor = new ModelSessionMonitor() {
        @Override
        public void monitor_create(final ModelSession modelSession) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.modelSession().monitor_create(modelSession);
                }
            });
        }

        @Override
        public void monitor_close(final ModelSession modelSession) {
            tellActor(new MonitorActor.MonitorMsg() {
                @Override
                public void monitorTo(AnkorSystemMonitor monitor) {
                    monitor.modelSession().monitor_close(modelSession);
                }
            });
        }
    };

    private void tellActor(MonitorActor.MonitorMsg msg) {
        monitorActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public SwitchboardMonitor switchboard() {
        return switchboardMonitor;
    }

    @Override
    public ModelSessionMonitor modelSession() {
        return modelSessionMonitor;
    }
}
