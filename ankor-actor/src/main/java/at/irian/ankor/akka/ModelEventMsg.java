package at.irian.ankor.akka;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class ModelEventMsg {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEventMsg.class);

    private final ModelContext modelContext;
    private final ModelEvent modelEvent;

    public ModelEventMsg(ModelContext modelContext, ModelEvent modelEvent) {
        this.modelContext = modelContext;
        this.modelEvent = modelEvent;
    }

    public ModelContext getModelContext() {
        return modelContext;
    }

    public ModelEvent getModelEvent() {
        return modelEvent;
    }
}
