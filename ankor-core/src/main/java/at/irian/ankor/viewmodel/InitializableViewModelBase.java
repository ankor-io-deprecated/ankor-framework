package at.irian.ankor.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;


/**
 * Auto-initializing convenient base class for view model objects implementing
 * {@link RefAware} A subclass must call
 * {@link #InitializableViewModelBase(Ref) super(ref)} in order to
 * auto-initialize {@link at.irian.ankor.ref.Ref} via
 * {@link AnkorPatterns#initViewModel} Inspired by {@link ViewModelBase}
 * 
 * @author Andy Maleh
 * @see CompositeViewModelBase
 * @see ViewModelBase
 */
public abstract class InitializableViewModelBase implements RefAware {

   protected Ref ref;

   /**
    * Subclasses must call via {@link #InitializableViewModelBase(Ref)
    * super(ref)} in order to auto-initialize
    * 
    * @param ref Ankor ref for view model remote syncing
    */
   protected InitializableViewModelBase(Ref ref) {
      initializeRef(ref);
   }
   
   /**
    * Optional no-arg constructor in case the ref is to be provided later
    * via {@link InitializableViewModelBase#initializeRef(Ref)}
    * 
    */
   protected InitializableViewModelBase() {
      //NOOP
   }

   @Override
   public Ref getRef() {
      return ref;
   }

   /**
    * Initializes this Ankor view model with a new Ref
    * 
    * @param ref Ankor ref for view model remote syncing
    */
   public void initializeRef(Ref ref) {
      if (ref != null) {
         this.ref = ref;
         AnkorPatterns.initViewModel(this, this.ref);
      }
   }

}
