package at.irian.ankor.monitor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import com.typesafe.config.Config;

/**
 * @author Thomas Spiegl
 */
public class AkkaMonitor implements Monitor {

    private final ActorRef monitorActor;

    protected AkkaMonitor(ActorRef monitorActor) {
        this.monitorActor = monitorActor;
    }

    public static Monitor create(ActorSystem actorSystem) {
        Config config = actorSystem.settings().config();
        ActorRef monitorActor = actorSystem.actorOf(MonitorActor.props(config),
                MonitorActor.name());
        return new AkkaMonitor(monitorActor);
    }

    @Override
    public void connect(final ModelAddress a, final ModelAddress b) {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.connect(a, b);
            }
        }, ActorRef.noSender());
    }

    @Override
    public void disconnect(final ModelAddress a, final ModelAddress b) {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.disconnect(a, b);
            }
        }, ActorRef.noSender());
    }

    @Override
    public void send(final ModelAddress sender, final EventMessage message, final ModelAddress receiver) {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.send(sender, message, receiver);
            }
        }, ActorRef.noSender());
    }

    @Override
    public void inboundMessage(final ModelAddress sender) {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.inboundMessage(sender);
            }
        }, ActorRef.noSender());
    }

    @Override
    public void outboundMessage(final ModelAddress receiver) {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.outboundMessage(receiver);
            }
        }, ActorRef.noSender());
    }

    @Override
    public void addModelSession() {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.addModelSession();
            }
        }, ActorRef.noSender());
    }

    @Override
    public void removeModelSession() {
        monitorActor.tell(new MonitorActor.MonitorMsg() {
            @Override
            public void writeTo(Monitor monitor) {
                monitor.removeModelSession();
            }
        }, ActorRef.noSender());
    }
}
