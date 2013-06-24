package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;

/**
 * @author Thomas Spiegl
 */
public interface ClickAction {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Action.class);

    void onClick(Ref valueRef);
}
