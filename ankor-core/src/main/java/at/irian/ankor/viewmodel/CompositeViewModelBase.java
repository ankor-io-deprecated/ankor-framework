package at.irian.ankor.viewmodel;

import java.util.List;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.switching.routing.DefaultServerRoutingLogic;

/**
 * Composite auto-initializing convenient base class for stateful application
 * view models that are composed of other child view models {@link #children()}
 * 
 * Extends {@link ViewModelBase}, so it is {@link RefAware} and will automatically
 * get its {@link Ref} re-initialized by {@link DefaultServerRoutingLogic} upon establishing 
 * a new Ankor session.
 * 
 * A subclass must override {@link #children()} to pass a list of child view
 * model objects of type {@link InitializableViewModelBase} to be used by call
 * to {@link #CompositeViewModelBase(Ref) super(ref)} in order to
 * auto-initialize {@link at.irian.ankor.ref.Ref} via
 * {@link AnkorPatterns#initViewModel}, including all children in a recursive
 * fashion.
 * 
 * Inspired by {@link ViewModelBase}
 * 
 * @author Andy Maleh
 * @see CompositeViewModelBase
 * @see ViewModelBase
 */
public abstract class CompositeViewModelBase extends InitializableViewModelBase {

    /**
     * Subclasses must call via {@link #InitializableViewModelBase(Ref)
     * super(ref)} in order to auto-initialize, including children (child view
     * models).
     * 
     * @param ref Ankor ref for view model remote syncing
     */
    protected CompositeViewModelBase(Ref ref) {
	super(ref);
    }

    /**
     * Returns child view models. Subclasses must override to run initializeRef
     * on all children recursively.
     * 
     * It assumes ref is initialized already, so implementation can call
     * {@link #getRef()} in order to build child view model refs via
     * {@link at.irian.ankor.ref.Ref#appendPath(String)}
     * 
     * @return children of ref model if any or an empty list otherwise
     */
    protected abstract List<InitializableViewModelBase> children();

    /**
     * Initializes this Ankor ViewModel children (child view models) 
     * recursively as part of calling {@link super#initializeRef(Ref)}
     * 
     * Assumes {@link Ref} instance has already been set and is
     * accessible via {@link super#getRef} 
     */
    @Override
    protected void initializePropertiesBeforeFirstSync() {
	Ref ref = getRef();
	for (InitializableViewModelBase childViewModel : children()) {
	    String childViewModelRefPath = childViewModel.getRef().path();
	    String childViewModelRelativeRefPath = childViewModelRefPath.replaceFirst(ref.path() + ".", "");
	    Ref childViewModelRef = ref.appendPath(childViewModelRelativeRefPath);
	    childViewModel.initializeRef(childViewModelRef);
	}
    }

}
