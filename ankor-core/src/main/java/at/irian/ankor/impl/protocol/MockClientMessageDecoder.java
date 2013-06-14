package at.irian.ankor.impl.protocol;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.protocol.ClientMessageDecoder;
import at.irian.ankor.impl.msgbus.MockClientMessage;

import java.io.Serializable;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MockClientMessageDecoder implements ClientMessageDecoder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultClientMessageDecoder.class);

    @Override
    public Serializable decodeModelId(Object clientToServerMessage) {
        return ((MockClientMessage)clientToServerMessage).getModelId();
    }

    @Override
    public List<ModelChange> decodeModelChanges(Object clientToServerMessage) {
        return ((MockClientMessage)clientToServerMessage).getModelChanges();
    }

    @Override
    public List<ModelAction> decodeModelActions(Object clientToServerMessage) {
        return ((MockClientMessage)clientToServerMessage).getModelActions();
    }
}
