package at.irian.ankor.api.protocol;

import at.irian.ankor.api.model.ModelChange;

import java.util.List;

/**
 */
public interface ClientMessageEncoder {

    Object encodeClientMessage(List<ModelChange> modelChanges);

}
