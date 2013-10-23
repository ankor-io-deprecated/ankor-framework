package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

/**
 * Marker interface for view model objects that know their own Ref.
 * @author Manfred Geiler
 */
public interface RefAware {
    /**
     * @return the {@link Ref} of this instance
     */
    Ref getRef();
}
