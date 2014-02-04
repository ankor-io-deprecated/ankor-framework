package at.irian.ankor.path;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface PathSyntax {

    String parentOf(String path);

    String concat(String path, String subPath);

    String addArrayIdx(String path, int index);

    String addLiteralMapKey(String path, String literalKey);

    String addPathMapKey(String path, String key);

    boolean isParentChild(String parent, String child);

    boolean isDescendant(String descendant, String ancestor);

    /**
     * Returns the "property" of the given path. Depending on the actual path node type this can be:
     * <ul>
     *     <li>the name of a bean property (e.g. getPropertyName("foo.bar") returns "bar")</li>
     *     <li>an index (e.g. getPropertyName("foo.bar[5]") returns "5")</li>
     *     <li>a literal map key (e.g. getPropertyName("foo['bar']") returns "bar")</li>
     * </ul>
     *
     * @return the "property" represented by the given path
     * @throws IllegalArgumentException if this path represents a value mapped by a path key (see {@link #addPathMapKey(String, String)}
     */
    String getPropertyName(String path);

    boolean isHasParent(String path);

    boolean isArrayIdx(String path);

    boolean isLiteralMapKey(String path);

    boolean isPathMapKey(String path);

    String rootOf(String path);

    boolean isEqual(String path1, String path2);
}
