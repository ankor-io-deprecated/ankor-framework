package at.irian.ankor.ref.match.pattern;

/**
 * Model for parsed matching pattern.
 *
 * @author Manfred Geiler
 */
public class Pattern {

    private final Pattern parent;
    private final Node property;

    public Pattern(Pattern parent, Node property) {
        this.parent = parent;
        this.property = property;
    }

    public Pattern parent() {
        return parent;
    }

    public Node property() {
        return property;
    }

    public int getBackRefCount() {
        int cnt = property.isBackref() ? 1 : 0;
        if (parent == null) {
            return cnt;
        } else {
            return parent.getBackRefCount() + cnt;
        }
    }

}
