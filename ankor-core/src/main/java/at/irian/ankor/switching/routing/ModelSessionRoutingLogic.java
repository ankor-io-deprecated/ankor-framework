package at.irian.ankor.switching.routing;

import at.irian.ankor.application.Application;
import at.irian.ankor.session.ModelSessionFactory;
import at.irian.ankor.switching.connector.local.LocalModelAddress;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ModelSessionRoutingLogic implements RoutingLogic {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSessionRoutingLogic.class);

    private final ModelSessionFactory modelSessionFactory;
    private final ModelSessionManager modelSessionManager;
    private final Application application;

    public ModelSessionRoutingLogic(ModelSessionFactory modelSessionFactory,
                                    ModelSessionManager modelSessionManager,
                                    Application application) {
        this.modelSessionFactory = modelSessionFactory;
        this.modelSessionManager = modelSessionManager;
        this.application = application;
    }

    @Override
    public ModelAddress findRoutee(ModelAddress sender, Map<String, Object> connectParameters) {

        if (connectParameters == null) {
            connectParameters = Collections.emptyMap();
        }
        LOG.info("Connect request received from {} with parameters {}", sender, connectParameters);

        String modelName = sender.getModelName();

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

        ModelAddress receiver = new LocalModelAddress(modelSession.getId(), modelName);

        if (receiver.equals(sender)) {
            throw new IllegalArgumentException("ModelSession must not connect to itself: " + modelSession);
        }

        return receiver;
    }
}
