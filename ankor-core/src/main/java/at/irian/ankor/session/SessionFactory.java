package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public interface SessionFactory {

    Session create(ModelContext modelContext, String sessionId);

    void close();
}
