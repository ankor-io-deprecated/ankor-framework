package at.irian.ankor.ref;

import at.irian.ankor.action.Action;

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
     * @return true, if the value of this Ref can be resolved;
     *         false, if resolving the value would cause an Exception (because the parent Ref no longer exists)
     */
    boolean isValid();

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
     * @param propertyOrSubPath name of a property or a valid property path (see {@link at.irian.ankor.path.PathSyntax})
     * @return Ref to the model object this path evaluates to relative to this Ref
     */
    Ref append(String propertyOrSubPath);

    /**
     * @param index index
     * @return Ref to the indexed array element of the model object referenced by this Ref
     */
    Ref appendIdx(int index);

    Ref appendLiteralKey(String literalKey);

    Ref appendPathKey(String pathKey);

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

    String propertyName();

    Ref ancestor(String ancestorPropertyName);

    boolean isRoot();

    RefContext context();

    Ref withRefContext(RefContext newRefContext);

    void fireAction(Action action);

    void addActionListener(ActionListener listener);

    void addValueChangeListener(ChangeListener listener);

    void addTreeChangeListener(ChangeListener listener);
}
