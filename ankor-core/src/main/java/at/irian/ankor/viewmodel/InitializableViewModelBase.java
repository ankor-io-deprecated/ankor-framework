package at.irian.ankor.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;

/**
 * Auto-initializing convenient base class for view model objects implementing
 * {@link RefAware}
 * 
 * A subclass must call {@link #InitializableViewModelBase(Ref) super(ref)} in
 * order to auto-initialize {@link at.irian.ankor.ref.Ref} via
 * {@link AnkorPatterns#initViewModel}
 * 
 * 
 * Inspired by {@link ViewModelBase}
 * 
 * 
 * @author Andy Maleh
 * @see CompositeViewModelBase
 * @see ViewModelBase
 */
public abstract class InitializableViewModelBase extends ViewModelBase {

    /**
     * Subclasses must call via {@link #InitializableViewModelBase(Ref)
     * super(ref)} in order to auto-initialize
     * 
     * @param ref Ankor {@link Ref} instance for view model 
     */
    protected InitializableViewModelBase(Ref ref) {
	super();
	initializeRef(ref);
    }

    /**
     * Initializes this Ankor view model with a new Ref.
     * 
     * Subclasses may consider {@link EoverridinginitializePropertiesBeforeFirstSync}
     * to ensure properties are properly initialized when setting a new {@link Ref}
     * instance.
     * 
     * @param ref Ankor ref for view model remote syncing
     */
    public void initializeRef(Ref ref) {
	setRef(ref);
	initializePropertiesBeforeFirstSync();
	AnkorPatterns.initViewModel(this, getRef());
    }

    /**
     * By optionally overriding this method, subclasses can ensure initializing 
     * properties before {@link AnkorPatterns#initViewModel} is called to have 
     * them go out as part of the initial sync.
     * 
     * This method is executed as part of {@link #initializeRef}
     */
    protected void initializePropertiesBeforeFirstSync() {
	// NO-OP by default
    }

}
