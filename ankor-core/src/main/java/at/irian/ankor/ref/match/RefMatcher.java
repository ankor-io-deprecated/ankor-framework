package at.irian.ankor.ref.match;

import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.Ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public Result match(Ref ref) {
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
            return NO_MATCH;
        }

        Ref parentRef = null;
        if (!ref.isRoot()) {
            parentRef = ref.parent();
        }
        if (parentRef != null) {

            if (parentPattern != null) {

                RefMatcher parentMatcher = this.withPattern(parentPattern);
                Result matchResult = parentMatcher.match(parentRef);
                if (matchResult.isMatch()) {
                    if (isWatchedProperty(propertyPattern)) {
                        ArrayList<Ref> refs = new ArrayList<Ref>(matchResult.getWatchedRefs());
                        refs.add(ref);
                        return new Result(true, refs);
                    } else {
                        return matchResult;
                    }
                }

            }

            if (isMultiWildcard(propertyPattern)) {
                return match(parentRef);
            }

        } else {

            if (parentPattern == null) {
                if (isWatchedProperty(propertyPattern)) {
                    return new Result(true, Collections.singletonList(ref));
                } else {
                    return new Result(true, Collections.<Ref>emptyList());
                }
            }

        }

        return NO_MATCH;
    }


    private boolean matchRefWithSinglePropertyPattern(Ref ref, String propertyPattern) {
        if (isWildcard(propertyPattern)) {
            return true;
        } else if (isTypeCondition(propertyPattern)) {
            String type = propertyPattern.substring(1, propertyPattern.length() - 1);
            Object refValue = ref.getValue();
            if (refValue == null) {
                return false;
            }
            Class<?> refType = refValue.getClass();
            return type.equals(refType.getSimpleName()) || type.equals(refType.getName());
        } else if (isWatchedProperty(propertyPattern)) {
            return getPropertyNameFor(ref).equals(propertyPattern.substring(1, propertyPattern.length() - 1));
        } else {
            return getPropertyNameFor(ref).equals(propertyPattern);
        }
    }

    private String getPropertyNameFor(Ref ref) {
        return ref.isRoot() ? ref.path() : ref.propertyName();
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

    private boolean isWatchedProperty(String pattern) {
        return pattern.startsWith("(") && pattern.endsWith(")");
    }


    private static Result NO_MATCH = new Result(false, Collections.<Ref>emptyList());

    public static class Result {
        private final boolean match;
        private final List<Ref> watchedRefs;

        public Result(boolean match, List<Ref> watchedRefs) {
            this.match = match;
            this.watchedRefs = watchedRefs;
        }

        public boolean isMatch() {
            return match;
        }

        public List<Ref> getWatchedRefs() {
            return watchedRefs;
        }
    }

}
