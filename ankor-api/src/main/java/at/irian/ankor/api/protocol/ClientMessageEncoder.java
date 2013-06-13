package at.irian.ankor.api.protocol;

import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.msgbus.ServerToClientMsg;

import java.util.List;

/**
 */
public interface ClientMessageEncoder {

    ServerToClientMsg encodeClientMessage(List<ModelChange> modelChanges);

}
