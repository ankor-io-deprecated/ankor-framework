package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

/**
 * Convenient base class for view model objects that implements {@link RefAware}.
 * @author Manfred Geiler
 * @author Andy Maleh
 */
public abstract class ViewModelBase implements RefAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelBase.class);

    private Ref ref;

    
    
    /**
     * Constructs an instance of {@link ViewModelBase}
     * 
     * When using this no-arg constructor, subclass must later call the protected
     * {@link #setRef} method in order to set a {@link Ref} instance.
     */
    protected ViewModelBase() {
        setRef(null);
    }
    
    
    /**
     * Convenience constructor that takes an Ankor {@link Ref} instance. 
     * 
     * It is OK to start with a null ref and set one later with the protected
     * {@link #setRef} method.
     * 
     * @param ref Ankor {@link Ref} instance representing this view model
     */
    protected ViewModelBase(Ref ref) {
        setRef(ref);
    }

    /**
     * Sets Ankor {@link Ref} instance.
     * 
     * @param ref Ankor {@link Ref} instance.
     * @return
     */
    protected Ref setRef(Ref ref) {
	return this.ref = ref;
    }

    @Override
    public Ref getRef() {
        return ref;
    }
}
