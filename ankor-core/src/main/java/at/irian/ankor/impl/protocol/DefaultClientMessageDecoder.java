package at.irian.ankor.impl.protocol;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.protocol.ClientMessageDecoder;

import java.io.Serializable;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultClientMessageDecoder implements ClientMessageDecoder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultClientMessageDecoder.class);

    @Override
    public Serializable decodeModelId(Object clientToServerMessage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ModelChange> decodeModelChanges(Object clientToServerMessage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ModelAction> decodeModelActions(Object clientToServerMessage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
