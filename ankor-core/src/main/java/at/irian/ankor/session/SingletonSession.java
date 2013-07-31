package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.ref.RefContext;

/**
 * Simple Session, typically used on client systems that handle only one view model at the same time.
 *
 * @author Manfred Geiler
 * @see SingletonSessionManager
 */
public class SingletonSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSession.class);

    private final String sessionId;
    private final ModelContext modelContext;
    private final RefContext refContext;

    public SingletonSession(String sessionId, ModelContext modelContext, RefContext refContext) {
        this.sessionId = sessionId;
        this.modelContext = modelContext;
        this.refContext = refContext;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {
    }

    @Override
    public ModelContext getModelContext() {
        return modelContext;
    }

    @Override
    public RefContext getRefContext() {
        return refContext;
    }

}
