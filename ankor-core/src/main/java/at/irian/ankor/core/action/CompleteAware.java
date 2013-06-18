package at.irian.ankor.core.action;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface CompleteAware {
    void complete(ModelRef actionContext);
}
