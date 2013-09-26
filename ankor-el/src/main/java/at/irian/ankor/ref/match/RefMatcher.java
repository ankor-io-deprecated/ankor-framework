package at.irian.ankor.ref.match;

import at.irian.ankor.ref.Ref;

import java.util.Collections;
import java.util.List;

/**
 * RefMatcher for a concrete pattern. Can match a Ref against this pattern.
 * Supports back references similar to regular expression backrefs.
 *
 * @author Manfred Geiler
 */
public interface RefMatcher {

    Result match(Ref ref, Ref contextRef);

    class Result {
        private final boolean match;
        private final List<Ref> backRefs;

        public Result(boolean match, List<Ref> backRefs) {
            this.match = match;
            this.backRefs = backRefs;
        }

        public boolean isMatch() {
            return match;
        }

        public List<Ref> getBackRefs() {
            return backRefs;
        }
    }

    static Result NO_MATCH = new Result(false, Collections.<Ref>emptyList());

}
