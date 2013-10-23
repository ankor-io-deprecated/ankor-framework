package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

/**
 * Convenient base class for view model objects that implements {@link RefAware}.
 * @author Manfred Geiler
 */
public abstract class ViewModelBase implements RefAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelBase.class);

    private final Ref ref;

    protected ViewModelBase(Ref ref) {
        this.ref = ref;
    }

    @Override
    public Ref getRef() {
        return ref;
    }
}
