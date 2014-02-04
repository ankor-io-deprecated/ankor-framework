package at.irian.ankor.viewmodel.metadata;

import at.irian.ankor.ref.match.RefMatcher;

/**
 * @author Manfred Geiler
 */
public class ActionListenerMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionListenerMetadata.class);

    private final String actionName;
    private final RefMatcher pattern;
    private final InvocationMetadata invocation;

    public ActionListenerMetadata(String actionName, RefMatcher pattern, InvocationMetadata invocation) {
        this.actionName = actionName;
        this.pattern = pattern;
        this.invocation = invocation;
    }

    public String getActionName() {
        return actionName;
    }

    public RefMatcher getPattern() {
        return pattern;
    }

    public InvocationMetadata getInvocation() {
        return invocation;
    }
}
