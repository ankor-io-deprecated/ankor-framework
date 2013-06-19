package at.irian.ankor.core.action;

import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface CompleteAware {
    void complete(Ref actionContext);
}
