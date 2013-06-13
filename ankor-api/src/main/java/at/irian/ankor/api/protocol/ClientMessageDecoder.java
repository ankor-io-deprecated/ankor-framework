package at.irian.ankor.api.protocol;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.msgbus.ClientToServerMsg;

import java.util.List;

/**
 */
public interface ClientMessageDecoder {

    List<ModelChange> decodeModelChanges(ClientToServerMsg clientToServerMessage);

    List<ModelAction> decodeModelActions(ClientToServerMsg clientToServerMessage);

}
