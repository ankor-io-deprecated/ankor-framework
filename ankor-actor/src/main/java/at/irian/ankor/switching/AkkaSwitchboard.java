package at.irian.ankor.switching;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.Broadcast;
import at.irian.ankor.switching.connector.ConcurrentConnectorRegistry;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.*;

import java.util.Map;

/**
* @author Manfred Geiler
*/
public class AkkaSwitchboard implements SwitchboardImplementor {

    private final ActorRef switchboardRouterActor;
    private final ConcurrentConnectorRegistry connectorRegistry;
    private final RoutingTable routingTable;
    private MySimpleSwitchboard delegateSwitchboard;

    protected AkkaSwitchboard(ActorRef switchboardRouterActor,
                              ConcurrentConnectorRegistry connectorRegistry,
                              RoutingTable routingTable) {
        this.switchboardRouterActor = switchboardRouterActor;
        this.connectorRegistry = connectorRegistry;
        this.routingTable = routingTable;
        this.delegateSwitchboard = new MySimpleSwitchboard(routingTable, connectorRegistry);
    }

    public static SwitchboardImplementor create(ActorSystem actorSystem) {

        ActorRef switchboardActor = actorSystem.actorOf(SwitchboardActor.props(actorSystem.settings().config()),
                                                        SwitchboardActor.name());
        ConcurrentConnectorRegistry connectorRegistry = new ConcurrentConnectorRegistry();
        RoutingTable routingTable = new ConcurrentRoutingTable();

        return new AkkaSwitchboard(switchboardActor, connectorRegistry, routingTable);
    }

    @Override
    public void setRoutingLogic(RoutingLogic routingLogic) {
        delegateSwitchboard.setRoutingLogic(routingLogic);
    }

    @Override
    public ConnectorRegistry getConnectorRegistry() {
        return connectorRegistry;
    }

    /**
     * This implementation immediately delegates to an actor pool and executes asynchronously.
     * Although executing in parallel the actor pool makes sure that "open" requests from the same sender
     * are handled serially in the correct order.
     *
     * @param sender  ModelAddress that request the connection
     * @param connectParameters  criteria for the {@link at.irian.ankor.switching.routing.RoutingLogic} to find the proper receiver
     */
    @Override
    public void openConnection(ModelAddress sender, Map<String, Object> connectParameters) {
        switchboardRouterActor.tell(new SwitchboardActor.OpenMsg(sender, connectParameters), ActorRef.noSender());
    }

    /**
     * This implementation immediately delegates to an actor pool and executes asynchronously.
     * Although executing in parallel the actor pool makes sure that "send" requests from the same sender
     * are handled serially in the correct order.
     *
     * @param sender   ModelAddress that sends the EventMessage
     * @param message  an EventMessage
     */
    @Override
    public void send(ModelAddress sender, EventMessage message) {
        switchboardRouterActor.tell(new SwitchboardActor.SendFromMsg(sender, message), ActorRef.noSender());
    }

    /**
     * This implementation immediately delegates to an actor pool and executes asynchronously.
     * Although executing in parallel the actor pool makes sure that "send" requests to the same receiver
     * are handled serially in the correct order.
     *
     * @param sender   ModelAddress that sends the EventMessage
     * @param message  an EventMessage
     * @param receiver ModelAddress that shall receive the EventMessage
     */
    @Override
    public void send(ModelAddress sender, EventMessage message, ModelAddress receiver) {
        switchboardRouterActor.tell(new SwitchboardActor.SendToMsg(sender, receiver, message), ActorRef.noSender());
    }

    /**
     * This implementation immediately delegates to an actor pool and executes asynchronously.
     * Although executing in parallel the actor pool makes sure that "close" requests from the same sender
     * are handled serially in the correct order.
     *
     * @param sender  ModelAddress that wants to close connections
     */
    @Override
    public void closeAllConnections(ModelAddress sender) {
        switchboardRouterActor.tell(new SwitchboardActor.CloseFromMsg(sender), ActorRef.noSender());
    }

    /**
     * This implementation immediately delegates to an actor pool and executes asynchronously.
     * Although executing in parallel the actor pool makes sure that "close" requests to the same receiver
     * are handled serially in the correct order.
     *
     * @param sender   ModelAddress that wants to close the connection
     * @param receiver ModelAddress that shall be informed about the closing of the connection
     */
    @Override
    public void closeConnection(ModelAddress sender, ModelAddress receiver) {
        switchboardRouterActor.tell(new SwitchboardActor.CloseToMsg(sender, receiver), ActorRef.noSender());
    }

    @Override
    public void start() {
        delegateSwitchboard.start();
        switchboardRouterActor.tell(new Broadcast(new SwitchboardActor.StartMsg(delegateSwitchboard)), ActorRef.noSender());
    }

    @Override
    public void stop() {
        for (ModelAddress p : routingTable.getAllConnectedAddresses()) {
            closeAllConnections(p);
        }
        switchboardRouterActor.tell(new Broadcast(new SwitchboardActor.StopMsg()), ActorRef.noSender());
        delegateSwitchboard.stop();
    }


    private class MySimpleSwitchboard extends AbstractSwitchboard {
        private MySimpleSwitchboard(RoutingTable routingTable, ConnectorMapping connectorMapping) {
            super(routingTable, connectorMapping);
        }

        @Override
        protected void dispatchableSend(ModelAddress originalSender, ModelAddress receiver, EventMessage message) {
            AkkaSwitchboard.this.send(originalSender, message, receiver);
        }

        @Override
        protected void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver) {
            AkkaSwitchboard.this.closeConnection(sender, receiver);
        }
    }


}
