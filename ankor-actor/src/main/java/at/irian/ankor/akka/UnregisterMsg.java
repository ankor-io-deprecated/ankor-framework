package at.irian.ankor.akka;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public class UnregisterMsg {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEventMsg.class);

    private final ModelContext modelContext;

    public UnregisterMsg(ModelContext modelContext) {
        this.modelContext = modelContext;
    }

    public ModelContext getModelContext() {
        return modelContext;
    }

}
