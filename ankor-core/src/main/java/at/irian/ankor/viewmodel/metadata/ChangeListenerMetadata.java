package at.irian.ankor.viewmodel.metadata;

import at.irian.ankor.ref.match.RefMatcher;

/**
 * @author Manfred Geiler
 */
public class ChangeListenerMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeListenerMetadata.class);

    private final RefMatcher pattern;
    private final InvocationMetadata invocation;

    public ChangeListenerMetadata(RefMatcher pattern, InvocationMetadata invocation) {
        this.pattern = pattern;
        this.invocation = invocation;
    }

    public RefMatcher getPattern() {
        return pattern;
    }

    public InvocationMetadata getInvocation() {
        return invocation;
    }
}
