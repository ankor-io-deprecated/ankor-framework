package at.irian.ankor.switching;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.monitor.nop.NopRoutingTableMonitor;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.connector.DefaultConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.DefaultRoutingTable;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Manfred Geiler
*/
public class AkkaAddressBoundSwitchboard implements SwitchboardImplementor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaAddressBoundSwitchboard.class);

    private final ActorSystem actorSystem;
    private final SwitchboardMonitor monitor;
    private final ConnectorRegistry connectorRegistry;
    private final Map<String, ActorRef> actors;
    private RoutingLogic routingLogic;

    protected AkkaAddressBoundSwitchboard(ActorSystem actorSystem,
                                          SwitchboardMonitor monitor,
                                          ConnectorRegistry connectorRegistry) {
        this.actorSystem = actorSystem;
        this.monitor = monitor;
        this.connectorRegistry = connectorRegistry;
        this.actors = new ConcurrentHashMap<String, ActorRef>();
    }

    public static SwitchboardImplementor create(ActorSystem actorSystem, SwitchboardMonitor monitor) {

        RoutingTable routingTable = new DefaultRoutingTable(new NopRoutingTableMonitor());
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForConcurrency(20);  //todo  configure

        return new AkkaAddressBoundSwitchboard(actorSystem, monitor, connectorRegistry);
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
        getSwitchboardActorFor(sender).tell(new AkkaAddressBoundSwitchboardActor.OpenMsg(sender, connectParameters),
                                            ActorRef.noSender());
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
        getSwitchboardActorFor(sender).tell(new AkkaAddressBoundSwitchboardActor.SendFromMsg(sender, message),
                                            ActorRef.noSender());
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
        getSwitchboardActorFor(receiver).tell(new AkkaAddressBoundSwitchboardActor.SendToMsg(sender, receiver, message),
                                              ActorRef.noSender());
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
        getSwitchboardActorFor(sender).tell(new AkkaAddressBoundSwitchboardActor.CloseFromMsg(sender),
                                            ActorRef.noSender());
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
        getSwitchboardActorFor(receiver).tell(new AkkaAddressBoundSwitchboardActor.CloseToMsg(sender, receiver),
                                              ActorRef.noSender());

        // todo close (stop) and kill bound actor ....
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        for (ModelAddress p : routingLogic.getAllConnectedRoutees()) {
            closeAllConnections(p);
        }

        // todo    kill all actors ....
    }


    private ActorRef getSwitchboardActorFor(ModelAddress address) {
        String addressKey = address.consistentHashKey();
        ActorRef actor = actors.get(addressKey);
        if (actor == null) {
            synchronized (actors) {  //todo  better with lock?
                actor = actors.get(addressKey);
                if (actor == null) {
                    if (routingLogic == null) {
                        throw new IllegalStateException("No routingLogic");
                    }
                    actor = actorSystem.actorOf(AkkaAddressBoundSwitchboardActor.props(actorSystem.settings().config(),
                                                                                       connectorRegistry,
                                                                                       monitor,
                                                                                       routingLogic,
                                                                                       AkkaAddressBoundSwitchboard.this),
                                                AkkaAddressBoundSwitchboardActor.name(addressKey));
                    actors.put(addressKey, actor);
                    LOG.info("Created Switchboard Actor {}", actor.path().name());
                }
            }
        }
        return actor;
    }


}
