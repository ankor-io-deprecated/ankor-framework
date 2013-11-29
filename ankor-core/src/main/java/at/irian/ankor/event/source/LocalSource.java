package at.irian.ankor.event.source;

import at.irian.ankor.context.ModelContext;

/**
 * Local source of an event.
 *
 * @author Manfred Geiler
 */
public class LocalSource implements Source {

    private final ModelContext modelContext;

    public LocalSource(ModelContext modelContext) {
        this.modelContext = modelContext;
    }

    public ModelContext getModelContext() {
        return modelContext;
    }
}
