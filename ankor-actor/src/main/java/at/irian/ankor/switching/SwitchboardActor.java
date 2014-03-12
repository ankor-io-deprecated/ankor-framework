package at.irian.ankor.switching;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SwitchboardActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SwitchboardActor.class);

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        int nrOfInstances = config.getInt("at.irian.ankor.switching.SwitchboardActor.poolSize");
        return Props.create(SwitchboardActor.class)
                    .withRouter(new ConsistentHashingRouter(nrOfInstances));
    }

    public static String name() {
        return "ankor_switchboard";
    }

    private Switchboard delegateSwitchboard;

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        try {
            if (msg instanceof StartMsg) {
                handleStart(((StartMsg) msg).getDelegateSwitchboard());
            } else if (msg instanceof StopMsg) {
                handleStop();
            } else if (msg instanceof OpenMsg) {
                handleOpen(((OpenMsg) msg).getSender(), ((OpenMsg) msg).getConnectParameters());
            } else if (msg instanceof SendFromMsg) {
                handleSendFrom(((SendFromMsg) msg).getSender(), ((SendFromMsg) msg).getMessage());
            } else if (msg instanceof SendToMsg) {
                handleSendTo(((SendToMsg) msg).getSender(), ((SendToMsg) msg).getMessage(), ((SendToMsg) msg).getReceiver());
            } else if (msg instanceof CloseFromMsg) {
                handleCloseFrom(((CloseFromMsg) msg).getSender());
            } else if (msg instanceof CloseToMsg) {
                handleCloseTo(((CloseToMsg) msg).getSender(), ((CloseToMsg) msg).getReceiver());
            } else {
                unhandled(msg);
            }
        } catch (Exception e) {
            LOG.error("Exception while handling " + msg, e);
        }
    }

    private void handleStart(Switchboard delegateSwitchboard) {
        if (this.delegateSwitchboard != null) {
            throw new IllegalStateException("Switchboard already set");
        }
        this.delegateSwitchboard = delegateSwitchboard;
    }

    private void handleStop() {
        this.delegateSwitchboard = null;
    }

    private void handleOpen(ModelAddress sender, Map<String, Object> connectParameters) {
        checkRunning();
        delegateSwitchboard.openConnection(sender, connectParameters);
    }

    private void handleSendFrom(ModelAddress sender, EventMessage message) {
        checkRunning();
        delegateSwitchboard.send(sender, message);
    }

    private void handleSendTo(ModelAddress sender, EventMessage message, ModelAddress receiver) {
        checkRunning();
        delegateSwitchboard.send(sender, message, receiver);
    }

    private void handleCloseFrom(ModelAddress sender) {
        checkRunning();
        delegateSwitchboard.closeAllConnections(sender);
    }

    private void handleCloseTo(ModelAddress sender, ModelAddress receiver) {
        checkRunning();
        delegateSwitchboard.closeConnection(sender, receiver);
    }

    private void checkRunning() {
        if (delegateSwitchboard == null) {
            throw new IllegalStateException("Not yet started");
        }
    }



    public static class StartMsg {
        private final Switchboard delegateSwitchboard;

        public StartMsg(Switchboard delegateSwitchboard) {
            this.delegateSwitchboard = delegateSwitchboard;
        }

        public Switchboard getDelegateSwitchboard() {
            return delegateSwitchboard;
        }

        @Override
        public String toString() {
            return "StartMsg";
        }
    }

    public static class StopMsg {
        @Override
        public String toString() {
            return "StopMsg";
        }
    }

    public static class OpenMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelAddress sender;
        private final Map<String, Object> connectParameters;

        public OpenMsg(ModelAddress sender, Map<String, Object> connectParameters) {
            this.sender = sender;
            this.connectParameters = connectParameters;
        }

        public ModelAddress getSender() {
            return sender;
        }

        public Map<String, Object> getConnectParameters() {
            return connectParameters;
        }

        @Override
        public Object consistentHashKey() {
            return sender;
        }

        @Override
        public String toString() {
            return "OpenMsg{" +
                   "sender=" + sender +
                   ", connectParameters=" + connectParameters +
                   '}';
        }
    }

    public static class SendFromMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelAddress sender;
        private final EventMessage message;

        public SendFromMsg(ModelAddress sender, EventMessage message) {
            this.sender = sender;
            this.message = message;
        }

        public ModelAddress getSender() {
            return sender;
        }

        public EventMessage getMessage() {
            return message;
        }

        @Override
        public Object consistentHashKey() {
            return sender;
        }

        @Override
        public String toString() {
            return "SendFromMsg{" +
                   "sender=" + sender +
                   ", message=" + message +
                   '}';
        }
    }

    public static class SendToMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelAddress sender;
        private final ModelAddress receiver;
        private final EventMessage message;

        public SendToMsg(ModelAddress sender, ModelAddress receiver, EventMessage message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
        }

        public ModelAddress getSender() {
            return sender;
        }

        public ModelAddress getReceiver() {
            return receiver;
        }

        public EventMessage getMessage() {
            return message;
        }

        @Override
        public Object consistentHashKey() {
            return receiver;
        }

        @Override
        public String toString() {
            return "SendToMsg{" +
                   "sender=" + sender +
                   ", receiver=" + receiver +
                   ", message=" + message +
                   '}';
        }
    }

    public static class CloseFromMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelAddress sender;

        public CloseFromMsg(ModelAddress sender) {
            this.sender = sender;
        }

        public ModelAddress getSender() {
            return sender;
        }

        @Override
        public Object consistentHashKey() {
            return sender;
        }

        @Override
        public String toString() {
            return "CloseMsg{" +
                   "sender=" + sender +
                   '}';
        }
    }

    public static class CloseToMsg implements ConsistentHashingRouter.ConsistentHashable {
        private final ModelAddress sender;
        private final ModelAddress receiver;

        public CloseToMsg(ModelAddress sender, ModelAddress receiver) {
            this.sender = sender;
            this.receiver = receiver;
        }

        public ModelAddress getSender() {
            return sender;
        }

        public ModelAddress getReceiver() {
            return receiver;
        }

        @Override
        public Object consistentHashKey() {
            return receiver;
        }

        @Override
        public String toString() {
            return "CloseMsg{" +
                   "sender=" + sender +
                   ", receiver=" + receiver +
                   '}';
        }
    }


}
