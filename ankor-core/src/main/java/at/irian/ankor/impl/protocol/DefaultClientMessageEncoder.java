package at.irian.ankor.impl.protocol;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.protocol.ClientMessageEncoder;
import at.irian.ankor.impl.msgbus.MockClientMessage;

import java.util.Collections;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultClientMessageEncoder implements ClientMessageEncoder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultClientMessageEncoder.class);

    @Override
    public Object encodeClientMessage(List<ModelChange> modelChanges) {
        return new MockClientMessage(modelChanges, Collections.<ModelAction>emptyList());
    }
}
