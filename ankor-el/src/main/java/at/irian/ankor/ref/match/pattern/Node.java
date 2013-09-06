package at.irian.ankor.ref.match.pattern;

/**
 * @author Manfred Geiler
 */
public class Node {

    private final String propertyName;
    private final boolean backref;
    private final WildcardType wildcardType;
    private final String javaType;

    public Node(String propertyName, WildcardType wildcardType, boolean backref, String javaType) {
        this.propertyName = propertyName;
        this.backref = backref;
        this.wildcardType = wildcardType;
        this.javaType = javaType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isBackref() {
        return backref;
    }

    public WildcardType getWildcardType() {
        return wildcardType;
    }

    public boolean isWildcard() {
        return wildcardType != null;
    }

    public boolean isMultiWildcard() {
        return wildcardType == WildcardType.multiNode;
    }

    public boolean isContextWildcard() {
        return wildcardType == WildcardType.context;
    }

    public boolean isSingleWildcard() {
        return wildcardType == WildcardType.singleNode;
    }

    public boolean isTypeWildcard() {
        return wildcardType == WildcardType.javaType;
    }

    public String getJavaType() {
        return javaType;
    }
}
