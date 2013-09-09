package at.irian.ankor.ref.match.pattern;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;

import java.util.ArrayList;
import java.util.Collections;

/**
 * RefMatcher implementation that is based on a {@link Pattern} model.
 *
 * @author Manfred Geiler
 */
public class PatternRefMatcher implements RefMatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefMatcher.class);

    private final Pattern pattern;

    public PatternRefMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    public RefMatcher withPattern(Pattern pattern) {
        return new PatternRefMatcher(pattern);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public Result match(Ref ref, Ref contextRef) {

        Node propertyPattern = pattern.property();
        Pattern parentPattern = pattern.parent();

        if (parentPattern == null) {
            // this is a root (single node) pattern
            return matchRefWithRootPattern(ref, propertyPattern, contextRef)
                   ? createResult(ref, propertyPattern.isBackref(), null)
                   : NO_MATCH;
        }

        // this is a non-root pattern...

        if (!matchRefWithNonRootPattern(ref, propertyPattern, contextRef)) {
            return NO_MATCH;
        }

        if (ref.isRoot()) {
            return NO_MATCH;
        }

        Ref parentRef = ref.parent();
        RefMatcher parentMatcher = this.withPattern(parentPattern);
        Result parentMatchResult = parentMatcher.match(parentRef, contextRef);
        if (parentMatchResult.isMatch()) {
            return createResult(ref, propertyPattern.isBackref(), parentMatchResult);
        }

        if (propertyPattern.isMultiWildcard()) {
            return match(parentRef, contextRef);
        }

        return NO_MATCH;
    }

    private Result createResult(Ref ref, boolean isBackref, Result parentResult) {
        if (isBackref) {
            if (parentResult != null && !parentResult.getBackRefs().isEmpty()) {
                ArrayList<Ref> backRefs = new ArrayList<Ref>(parentResult.getBackRefs());
                backRefs.add(ref);
                return new Result(true, backRefs);
            } else {
                return new Result(true, Collections.singletonList(ref));
            }
        } else {
            if (parentResult != null) {
                return parentResult;
            } else {
                return new Result(true, Collections.<Ref>emptyList());
            }
        }
    }

    private boolean matchRefWithRootPattern(Ref ref, Node propertyPattern, Ref contextRef) {

        if (propertyPattern.isMultiWildcard()) {
            return true;
        }

        if (propertyPattern.isContextWildcard()) {
            return contextRef != null && ref.path().equals(contextRef.path());
        }

        // neither ** nor @
        // --> must be root the match the pattern
        if (!ref.isRoot()) {
            return false;
        }

        if (propertyPattern.isSingleWildcard()) {
            return ref.isRoot();
        }

        if (propertyPattern.isTypeWildcard()) {
            String type = propertyPattern.getJavaType();
            Object refValue = ref.getValue();
            if (refValue == null) {
                return false;
            }
            Class<?> refType = refValue.getClass();
            return type.equals(refType.getSimpleName()) || type.equals(refType.getName());
        }

        String refPropName = ref.path();
        return refPropName.equals(propertyPattern.getPropertyName());
    }

    private boolean matchRefWithNonRootPattern(Ref ref, Node propertyPattern, Ref contextRef) {

        if (propertyPattern.isMultiWildcard()) {
            return true;
        }

        if (propertyPattern.isSingleWildcard()) {
            return true;
        }

        if (propertyPattern.isContextWildcard()) {
            return contextRef != null && ref.path().equals(contextRef.path());
        }

        if (propertyPattern.isTypeWildcard()) {
            String type = propertyPattern.getJavaType();
            Object refValue = ref.getValue();
            if (refValue == null) {
                return false;
            }
            Class<?> refType = refValue.getClass();
            return type.equals(refType.getSimpleName()) || type.equals(refType.getName());
        }

        String refPropName = ref.isRoot() ? ref.path() : ref.propertyName();
        return refPropName.equals(propertyPattern.getPropertyName());
    }

}
