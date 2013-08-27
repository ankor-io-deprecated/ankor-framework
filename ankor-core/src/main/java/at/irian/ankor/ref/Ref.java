package at.irian.ankor.ref;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface Ref {

    /**
     * Change the value of the underlying model object.
     * @param value the new value
     */
    void setValue(Object value);

    /**
     * Get the value of the underlying model object.
     * @param <T>  type of the model field (for convenience only, so that there is no explicit cast needed)
     * @return value of the underlying model field
     * @throws IllegalStateException if value is not valid
     */
    <T> T getValue();

    /**
     * @return true, if the value of this Ref can be resolved;
     *         false, if resolving the value would cause an IllegalStateException (because the parent Ref no longer exists)
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
     * @see at.irian.ankor.path.PathSyntax#concat(String, String)
     */
    Ref append(String propertyOrSubPath);

    /**
     * @param index index
     * @return Ref to the indexed array element of the model object of type {@link java.util.List} or an array referenced by this Ref
     * @see at.irian.ankor.path.PathSyntax#addArrayIdx(String, int)
     */
    Ref appendIdx(int index);

    /**
     * @param literalKey literal {@link String} key
     * @return Ref to the mapped value of the model object of type {@link java.util.Map} referenced by this Ref
     * @see at.irian.ankor.path.PathSyntax#addLiteralMapKey(String, String)
     */
    Ref appendLiteralKey(String literalKey);

    /**
     * @param pathKey  key, which is itself a path that must resolve to a literal {@link String} key
     * @return Ref to the mapped value of the model object of type {@link java.util.Map} referenced by this Ref
     * @see at.irian.ankor.path.PathSyntax#addPathMapKey(String, String)
     */
    Ref appendPathKey(String pathKey);

    /**
     * @return the full structural path of this Ref
     * @see at.irian.ankor.path.PathSyntax
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

    /**
     * Returns the "property" this Ref is referencing. Depending on the actual path node type this can be:
     * <ul>
     *     <li>the name of a bean property (e.g. refFactory.ref("foo.bar").propertyName() returns "bar")</li>
     *     <li>an index (e.g. refFactory.ref("foo.bar[5]").propertyName() returns "5")</li>
     *     <li>a literal map key (e.g. refFactory.ref("foo['bar']").propertyName() returns "bar")</li>
     * </ul>
     *
     * @return the name of the (bean) property this Ref is referencing
     * @see at.irian.ankor.path.PathSyntax#getPropertyName(String)
     * @throws IllegalArgumentException if this Ref references a value mapped by a path key (see {@link #appendPathKey(String)}
     */
    String propertyName();

    Ref ancestor(String ancestorPropertyName);

    boolean isRoot();

    RefContext context();

    void fire(Action action);

    @Deprecated
    void fire(ModelEvent event);

    void delete();

    void delete(Object keyOrIndex);

    void insert(int idx, Object value);
}
