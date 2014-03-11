package at.irian.ankor.switching;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.Broadcast;
import at.irian.ankor.switching.connector.ConcurrentConnectorRegistry;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ConcurrentRoutingTable;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;

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
        ConcurrentRoutingTable routingTable = new ConcurrentRoutingTable();

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

    @Override
    public void openConnection(ModelAddress sender, Map<String, Object> connectParameters) {
        switchboardRouterActor.tell(new SwitchboardActor.OpenMsg(sender, connectParameters), ActorRef.noSender());
    }

    @Override
    public void send(ModelAddress sender, EventMessage message) {
        switchboardRouterActor.tell(new SwitchboardActor.SendMsg(sender, message), ActorRef.noSender());
    }

    @Override
    public void send(ModelAddress sender, ModelAddress receiver, EventMessage message) {
        switchboardRouterActor.tell(new SwitchboardActor.SendMsg(sender, receiver, message), ActorRef.noSender());
    }

    @Override
    public void closeAllConnections(ModelAddress sender) {
        switchboardRouterActor.tell(new SwitchboardActor.CloseMsg(sender), ActorRef.noSender());
    }

    @Override
    public void closeConnection(ModelAddress sender, ModelAddress receiver) {
        switchboardRouterActor.tell(new SwitchboardActor.CloseMsg(sender, receiver), ActorRef.noSender());
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
            AkkaSwitchboard.this.send(originalSender, receiver, message);
        }

        @Override
        protected void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver) {
            AkkaSwitchboard.this.closeConnection(sender, receiver);
        }
    }


}
