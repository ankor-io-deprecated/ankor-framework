package at.irian.ankor.switching.routing;

import at.irian.ankor.application.Application;
import at.irian.ankor.monitor.nop.NopRoutingTableMonitor;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionFactory;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.connector.local.StatefulSessionModelAddress;
import at.irian.ankor.switching.connector.local.StatelessSessionModelAddress;

import java.util.*;

/**
 * RoutingLogic that connects external nodes to a local ModelSession.
 * This RoutingLogic implements the standard behaviour for server systems.
 *
 * @author Manfred Geiler
 */
public class DefaultServerRoutingLogic implements RoutingLogic {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultServerRoutingLogic.class);

    private final ModelSessionFactory modelSessionFactory;
    private final ModelSessionManager modelSessionManager;
    private final List<Application> applications;
    private final RoutingTable routingTable;

    public DefaultServerRoutingLogic(ModelSessionFactory modelSessionFactory,
                                     ModelSessionManager modelSessionManager) {
        this(modelSessionFactory, modelSessionManager, Collections.<Application>emptyList());
    }

    protected DefaultServerRoutingLogic(ModelSessionFactory modelSessionFactory,
                                        ModelSessionManager modelSessionManager,
                                        List<Application> applications) {
        this.modelSessionFactory = modelSessionFactory;
        this.modelSessionManager = modelSessionManager;
        this.routingTable = new DefaultRoutingTable(new NopRoutingTableMonitor());
        this.applications = applications;
    }

    public DefaultServerRoutingLogic withApplication(Application application) {
        List<Application> newList = new ArrayList<Application>(applications);
        newList.add(application);
        return new DefaultServerRoutingLogic(modelSessionFactory, modelSessionManager, newList);
    }


    @Override
    public ModelAddress connect(ModelAddress sender, Map<String, Object> connectParameters) {

        if (sender instanceof StatelessSessionModelAddress) {
            throw new IllegalArgumentException("A StatelessSessionModelAddress is not allowed to connect: " + sender);
        }

        if (connectParameters == null) {
            connectParameters = Collections.emptyMap();
        }
        LOG.info("Connect request received from {} with parameters {}", sender, connectParameters);

        String modelName = sender.getModelName();
        Application application = getApplicationFor(modelName);

        if (application.isStateless()) {
            return getStatelessReceiver(sender);
        } else {
            return getStatefulReceiver(sender, connectParameters, modelName, application);
        }
    }

    private ModelAddress getStatelessReceiver(ModelAddress sender) {
        ModelAddress receiver = new StatelessSessionModelAddress(sender);
        if (receiver.equals(sender)) {
            // todo   this is a paranoia check, right?
            throw new IllegalArgumentException("StatelessSessionModelAddress must not connect to itself: " + sender);
        }
        return receiver;
    }

    private ModelAddress getStatefulReceiver(ModelAddress sender,
                                             Map<String, Object> connectParameters,
                                             String modelName, Application application) {
        ModelSession modelSession = getOrCreateStatefulSession(modelName, connectParameters, application);
        ModelAddress receiver = new StatefulSessionModelAddress(modelSession, modelName);
        if (receiver.equals(sender)) {
            throw new IllegalArgumentException("ModelSession must not connect to itself: " + modelSession);
        }
        boolean success = routingTable.connect(sender, receiver);
        if (!success) {
            LOG.warn("Already connected: {} and {}", sender, receiver);
        }
        return receiver;
    }

    private ModelSession getOrCreateStatefulSession(String modelName,
                                                    Map<String, Object> connectParameters,
                                                    Application application) {
        Object modelRoot = application.lookupModel(modelName, connectParameters);
        ModelSession modelSession;
        //todo  check if we have a concurrency issue here
        if (modelRoot == null) {
            modelSession = modelSessionFactory.createModelSession();
            modelRoot = application.createModel(modelName, connectParameters, modelSession.getRefContext());
            modelSession.setModelRoot(modelName, modelRoot);
            modelSessionManager.add(modelSession);
        } else {
            modelSession = modelSessionManager.findByModelRoot(modelRoot);
            if (modelSession == null) {
                LOG.warn("Could not find ModelSession for model root {} - most likely a timeout had happened, creating a new session...",
                         modelRoot);
                modelSession = modelSessionFactory.createModelSession();
                modelSession.setModelRoot(modelName, modelRoot);
                modelSessionManager.add(modelSession);
            }
        }
        return modelSession;
    }

    private Application getApplicationFor(String modelName) {
        Application application = null;
        for (Application app : applications) {
            if (app.getKnownModelNames().contains(modelName)) {
                if (application != null) {
                    LOG.warn("Multiple applications found that support models with name {}", modelName);
                } else {
                    application = app;
                }
            }
        }
         if (application == null) {
            throw new IllegalArgumentException("Could not find application for model {}" + modelName);
        }
        return application;
    }


    @Override
    public Collection<ModelAddress> getConnectedRoutees(ModelAddress sender) {
        if (sender instanceof StatelessSessionModelAddress) {
            // sender is a local stateless(!) model
            // --> the client that initiated this stateless session is associated with the address
            return Collections.singleton(((StatelessSessionModelAddress) sender).getClientAddress());
        } else if (sender instanceof StatefulSessionModelAddress) {
            // sender is a local stateful model
            // --> the clients are listed in the routing table
            return routingTable.getConnectedAddresses(sender);
        } else {
            // sender is an external client model
            String modelName = sender.getModelName();
            Application application = getApplicationFor(modelName);
            if (application.isStateless()) {
                // receiving application is stateless
                // --> return a StatelessSessionModelAddress that holds this client
                StatelessSessionModelAddress receiver = new StatelessSessionModelAddress(sender);
                return Collections.<ModelAddress>singleton(receiver);
            } else {
                // receiving application is stateful
                // --> the local session is listed in the routing table
                return routingTable.getConnectedAddresses(sender);
            }
        }
    }

    @Override
    public Collection<ModelAddress> getAllConnectedRoutees() {
        return routingTable.getAllConnectedAddresses();
    }

    @Override
    public void disconnect(ModelAddress sender, ModelAddress receiver) {
        routingTable.disconnect(sender, receiver);
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {
        routingTable.clear();
    }
}
