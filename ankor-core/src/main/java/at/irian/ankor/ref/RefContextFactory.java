package at.irian.ankor.ref;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public interface RefContextFactory {

    RefContext createRefContextFor(ModelContext modelContext);

}
