package at.irian.ankor.viewmodel;

import java.util.List;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;


/**
 * Composite auto-initializing convenient base class for view model objects implementing {@link RefAware}
 * composed of other child view model objects {@link #children()}
 * 
 * A subclass must override {@link #children()} to pass a list of child view model objects of type
 * {@link InitializableViewModelBase} to be used by call to {@link #CompositeViewModelBase(Ref) super(ref)}
 * in order to auto-initialize {@link at.irian.ankor.ref.Ref} via {@link AnkorPatterns#initViewModel}, 
 * including all children in a recursive fashion.
 *  
 * Inspired by {@link ViewModelBase}
 * 
 * @author Andy Maleh
 * @see CompositeViewModelBase
 * @see ViewModelBase
 */
public abstract class CompositeViewModelBase extends InitializableViewModelBase {

   /**
    * Subclasses must call via {@link #InitializableViewModelBase(Ref) super(ref)} in order
    * to auto-initialize, including children (child view models).
    *     
    * @param ref Ankor ref for view model remote syncing
    */
   protected CompositeViewModelBase(Ref ref) {
      super(ref);
   }
   
   /**
    * Optional no-arg constructor in case the ref is to be provided later
    * via {@link CompositeViewModelBase#initializeRef(Ref)}
    * 
    */
   protected CompositeViewModelBase() {
      //NOOP
   }

   /**
    * Returns child view models. Subclasses must override to run
    * initializeRef on all children recursively.
    * 
    * It assumes ref is initialized already, so implementation can
    * call {@link #getRef()} in order to build child view model refs via
    * {@link at.irian.ankor.ref.Ref#appendPath(String)}
    * 
    * @return children of ref model if any or an empty list otherwise
    */
   protected abstract List<? extends InitializableViewModelBase> children();

   /**
    * Initializes this Ankor ViewModel with a new Ref as well as all its
    * children (child view models)
    * 
    * @param ref Ankor ref for view model remote syncing
    */
   public void initializeRef(Ref ref) {
      this.ref = ref;
      for (InitializableViewModelBase childViewModel : children()) {
         Ref childViewModelRef = ref.appendPath(childViewModel.getRef().path().replaceFirst(ref.path() + ".", "")); 
         childViewModel.initializeRef(childViewModelRef);
      }
      AnkorPatterns.initViewModel(this, getRef());
   }

}
