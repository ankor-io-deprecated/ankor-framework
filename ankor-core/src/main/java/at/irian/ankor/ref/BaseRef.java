package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public abstract class BaseRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseRef.class);

    protected final RefContext refContext;

    public BaseRef(RefContext refContext) {
        this.refContext = refContext;
    }

    @Override
    public RefContext refContext() {
        return refContext;
    }

    @Override
    public Ref withModelContext(Ref modelContext) {
        return withRefContext(refContext.withModelContext(modelContext));
    }

    @Override
    public String toString() {
        return "Ref{" + path() + '}';
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        Ref parentRef = parent();
        return parentRef != null && (parentRef.equals(ref) || parentRef.isDescendantOf(ref));
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ref.isDescendantOf(this);
    }

}
