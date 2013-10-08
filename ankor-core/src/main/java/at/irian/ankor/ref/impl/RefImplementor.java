package at.irian.ankor.ref.impl;

import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

/**
 * Extends the Ref API by internal methods and functions that are only important for the Ankor system itself
 * but not for the application programmer.
 *
 * @author Manfred Geiler
 */
public interface RefImplementor extends Ref {

    void apply(Change change);

    void signal(Change change);

    void internalSetValue(Object newUnwrappedValue);

}
