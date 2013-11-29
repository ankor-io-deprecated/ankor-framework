package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;

/**
 * Extends the Ref API by internal methods and functions that are only important for the Ankor system itself
 * but not for the application programmer.
 *
 * @author Manfred Geiler
 */
public interface RefImplementor extends Ref {

    void apply(Source source, Change change);

    void signal(Source source, Change change);

    void internalSetValue(Object newUnwrappedValue);

    void fire(Source source, Action action);
}
