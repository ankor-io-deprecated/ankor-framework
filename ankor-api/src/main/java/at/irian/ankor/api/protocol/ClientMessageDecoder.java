package at.irian.ankor.api.protocol;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;

import java.util.List;

/**
 */
public interface ClientMessageDecoder {

    List<ModelChange> decodeModelChanges(Object clientToServerMessage);

    List<ModelAction> decodeModelActions(Object clientToServerMessage);

}
