package at.irian.ankor.core.ref;

import at.irian.ankor.core.action.ModelAction;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface Ref {

    /**
     * Change the value of the underlying model object.
     * @param value the new value
     */
    void setValue(Object value);

    /**
     * Get the value of the underlying model object.
     * @param <T>  type of the model field (for convenience only, no explicit cast needed)
     * @return value of the underlying model field
     */
    <T> T getValue();

    /**
     * @return Ref to the root object of the underlying model this Ref belongs to
     */
    Ref root();

    /**
     * @return Ref to the model object that is the structural parent of the object referenced by this Ref,
     *         null if this Ref is the root
     */
    Ref parent();

    /**
     * @param subPath name of a property or a valid property path (see TBD for supported path syntax)
     * @return Ref to the model object this path evaluates to relative to this Ref
     */
    Ref sub(String subPath);

    /**
     * Returns an unwatched Ref for the model object referenced by this Ref. Calls to the {@link #setValue(Object)}
     * method of an unwatched Ref do not cause a change event.
     * @return unwatched Ref to the model object referenced by this Ref
     */
    Ref unwatched();

    /**
     * Fire an action with this Ref as the action context.
     * @param action an Action
     */
    void fire(ModelAction action);

    /**
     * @return the full structural path of this Ref
     */
    String path();

    /**
     * @return true, if the given Ref is a parent (or grandparent, or...) of this Ref
     */
    boolean isDescendantOf(Ref ref);

    /**
     * @return true, if the given Ref is a child (or grandchild, or...) of this Ref
     */
    boolean isAncestorOf(Ref ref);

    boolean isRoot();

    RefContext refContext();
}
