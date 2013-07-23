package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;

/**
 * @author Manfred Geiler
 */
public class RefMatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefMatcher.class);

    private final PathSyntax pathSyntax;
    private final String pattern;

    public RefMatcher(PathSyntax pathSyntax, String pattern) {
        this.pathSyntax = pathSyntax;
        this.pattern = pattern;
    }

    public RefMatcher withPattern(String pattern) {
        return new RefMatcher(pathSyntax, pattern);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean matches(Ref ref) {
        String propertyPattern;
        String parentPattern;
        if (pattern.endsWith(">")) {
            int i = pattern.lastIndexOf('<');
            if (i < 0) {
                throw new IllegalArgumentException(pattern);
            }
            propertyPattern = pattern.substring(i);
            parentPattern = i == 0 ? null : pattern.substring(0, i - 1);
        } else if (pathSyntax.isHasParent(pattern)) {
            propertyPattern = pathSyntax.getPropertyName(pattern);
            parentPattern = pathSyntax.parentOf(pattern);
        } else {
            propertyPattern = pattern;
            parentPattern = null;
        }

        if (!matchRefWithSinglePropertyPattern(ref, propertyPattern)) {
            return false;
        }

        Ref parentRef = ref.parent();
        if (parentRef != null) {

            if (parentPattern != null) {

                RefMatcher parentMatcher = this.withPattern(parentPattern);
                if (parentMatcher.matches(parentRef)) {
                    return true;
                }

            }

            if (isMultiWildcard(propertyPattern)) {
                return matches(parentRef);
            }

        } else {

            if (parentPattern == null) {
                return true;
            }

        }

        return false;
    }


    @SuppressWarnings("SimplifiableIfStatement")
    private boolean matchRootProperty(Ref rootRef) {
        if (pathSyntax.isHasParent(pattern)) {
            return false;
        } else {
            return matchRefWithSinglePropertyPattern(rootRef, pattern);
        }
    }


    private boolean matchRefWithSinglePropertyPattern(Ref ref, String propertyPattern) {
        if (isWildcard(propertyPattern)) {
            return true;
        } else if (isTypeCondition(propertyPattern)) {
            String type = getType(propertyPattern);
            Object refValue = ref.getValue();
            if (refValue == null) {
                return false;
            }
            Class<?> refType = refValue.getClass();
            return type.equals(refType.getSimpleName()) || type.equals(refType.getName());
        } else {
            return ref.propertyName().equals(propertyPattern);
        }
    }

    private boolean isWildcard(String pattern) {
        return pattern.equals("*") || pattern.equals("**");
    }

    private boolean isMultiWildcard(String pattern) {
        return pattern.equals("**");
    }

    private boolean isTypeCondition(String pattern) {
        return pattern.startsWith("<") && pattern.endsWith(">");
    }

    private String getType(String propertyPattern) {
        return propertyPattern.substring(1, propertyPattern.length() - 1);
    }

}
