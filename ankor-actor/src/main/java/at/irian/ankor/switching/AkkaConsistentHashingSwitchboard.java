package at.irian.ankor.switching;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.Broadcast;
import at.irian.ankor.monitor.nop.NopRoutingTableMonitor;
import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.connector.DefaultConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ConcurrentRoutingTable;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;
import com.typesafe.config.Config;

import java.util.Map;

/**
* @author Manfred Geiler
*/
public class AkkaConsistentHashingSwitchboard implements SwitchboardImplementor {

    private final ActorRef switchboardRouterActor;
    private final ConnectorRegistry connectorRegistry;
    private final RoutingTable routingTable;
    private RoutingLogic routingLogic;

    protected AkkaConsistentHashingSwitchboard(ActorRef switchboardRouterActor,
                                               ConnectorRegistry connectorRegistry,
                                               RoutingTable routingTable) {
        this.switchboardRouterActor = switchboardRouterActor;
        this.connectorRegistry = connectorRegistry;
        this.routingTable = routingTable;
    }

    public static SwitchboardImplementor create(ActorSystem actorSystem, SwitchboardMonitor monitor) {

        Config config = actorSystem.settings().config();
        int nrOfInstances = config.getInt("at.irian.ankor.switching.AkkaConsistentHashingSwitchboardActor.poolSize");

        RoutingTable routingTable = new ConcurrentRoutingTable(new NopRoutingTableMonitor());
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForConcurrency(nrOfInstances);
        ActorRef switchboardRouterActor = actorSystem.actorOf(AkkaConsistentHashingSwitchboardActor.props(config,
                                                                                                          routingTable,
                                                                                                          connectorRegistry,
                                                                                                          monitor),
                                                              AkkaConsistentHashingSwitchboardActor.name());

        return new AkkaConsistentHashingSwitchboard(switchboardRouterActor, connectorRegistry, routingTable);
    }

    @Override
    public void setRoutingLogic(RoutingLogic routingLogic) {
        this.routingLogic = routingLogic;
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
        switchboardRouterActor.tell(new AkkaConsistentHashingSwitchboardActor.OpenMsg(sender, connectParameters), ActorRef.noSender());
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
        switchboardRouterActor.tell(new AkkaConsistentHashingSwitchboardActor.SendFromMsg(sender, message), ActorRef.noSender());
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
        switchboardRouterActor.tell(new AkkaConsistentHashingSwitchboardActor.SendToMsg(sender, receiver, message), ActorRef.noSender());
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
        switchboardRouterActor.tell(new AkkaConsistentHashingSwitchboardActor.CloseFromMsg(sender), ActorRef.noSender());
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
        switchboardRouterActor.tell(new AkkaConsistentHashingSwitchboardActor.CloseToMsg(sender, receiver), ActorRef.noSender());
    }

    @Override
    public void start() {
        switchboardRouterActor.tell(new Broadcast(new AkkaConsistentHashingSwitchboardActor.StartMsg(routingLogic)),
                                    ActorRef.noSender());
    }

    @Override
    public void stop() {
        for (ModelAddress p : routingTable.getAllConnectedAddresses()) {
            closeAllConnections(p);
        }
        switchboardRouterActor.tell(new Broadcast(new AkkaConsistentHashingSwitchboardActor.StopMsg()), ActorRef.noSender());
    }


}
