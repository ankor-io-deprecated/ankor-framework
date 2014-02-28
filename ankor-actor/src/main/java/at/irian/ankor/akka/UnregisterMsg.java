package at.irian.ankor.akka;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class UnregisterMsg {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEventMsg.class);

    private final ModelSession modelSession;

    public UnregisterMsg(ModelSession modelSession) {
        this.modelSession = modelSession;
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

}
