package at.irian.ankor.switching;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter;
import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.connector.SimpleHandlerScopeContext;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class AkkaAddressBoundSwitchboardActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaAddressBoundSwitchboardActor.class);

    public static Props props(@SuppressWarnings("UnusedParameters") Config config,
                              RoutingTable routingTable,
                              ConnectorMapping connectorMapping,
                              SwitchboardMonitor monitor,
                              RoutingLogic routingLogic,
                              Switchboard mainSwitchboard) {
        return Props.create(AkkaAddressBoundSwitchboardActor.class, routingTable, connectorMapping, monitor, routingLogic, mainSwitchboard);
    }

    public static String name(String addressKey) {
        return "ankor_switchboard_" + addressKey;
    }

    private final Switchboard mainSwitchboard;
    private final MySwitchboard mySwitchboard;

    public AkkaAddressBoundSwitchboardActor(RoutingTable routingTable,
                                            ConnectorMapping connectorMapping,
                                            SwitchboardMonitor monitor,
                                            RoutingLogic routingLogic,
                                            Switchboard mainSwitchboard) {
        this.mainSwitchboard = mainSwitchboard;
        HandlerScopeContext handlerScopeContext = new SimpleHandlerScopeContext();
        this.mySwitchboard = new MySwitchboard(routingTable, connectorMapping, handlerScopeContext, monitor);
        this.mySwitchboard.setRoutingLogic(routingLogic);
        this.mySwitchboard.start();  // todo  do with StartMsg?
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        LOG.debug("{} received {}", self(), msg);
        try {
            if (msg instanceof OpenMsg) {
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

    private void handleOpen(ModelAddress sender, Map<String, Object> connectParameters) {
        mySwitchboard.openConnection(sender, connectParameters);
    }

    private void handleSendFrom(ModelAddress sender, EventMessage message) {
        mySwitchboard.send(sender, message);
    }

    private void handleSendTo(ModelAddress sender, EventMessage message, ModelAddress receiver) {
        mySwitchboard.send(sender, message, receiver);
    }

    private void handleCloseFrom(ModelAddress sender) {
        mySwitchboard.closeAllConnections(sender);
    }

    private void handleCloseTo(ModelAddress sender, ModelAddress receiver) {
        mySwitchboard.closeConnection(sender, receiver);
    }


    public static class OpenMsg {
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
        public String toString() {
            return "OpenMsg{" +
                   "sender=" + sender +
                   ", connectParameters=" + connectParameters +
                   '}';
        }
    }

    public static class SendFromMsg {
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
        public String toString() {
            return "SendFromMsg{" +
                   "sender=" + sender +
                   ", message=" + message +
                   '}';
        }
    }

    public static class SendToMsg {
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
        public String toString() {
            return "SendToMsg{" +
                   "sender=" + sender +
                   ", receiver=" + receiver +
                   ", message=" + message +
                   '}';
        }
    }

    public static class CloseFromMsg {
        private final ModelAddress sender;

        public CloseFromMsg(ModelAddress sender) {
            this.sender = sender;
        }

        public ModelAddress getSender() {
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
            return receiver.consistentHashKey();
        }

        @Override
        public String toString() {
            return "CloseMsg{" +
                   "sender=" + sender +
                   ", receiver=" + receiver +
                   '}';
        }
    }



    private class MySwitchboard extends AbstractSwitchboard {
        private MySwitchboard(RoutingTable routingTable,
                              ConnectorMapping connectorMapping,
                              HandlerScopeContext handlerScopeContext,
                              SwitchboardMonitor monitor) {
            super(routingTable, connectorMapping, handlerScopeContext, monitor);
        }

        @Override
        protected void setRoutingLogic(RoutingLogic routingLogic) {
            super.setRoutingLogic(routingLogic);
        }

        @Override
        protected void dispatchableSend(ModelAddress originalSender, ModelAddress receiver, EventMessage message) {
            mainSwitchboard.send(originalSender, message, receiver);
        }

        @Override
        protected void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver) {
            mainSwitchboard.closeConnection(sender, receiver);
        }
    }

}
