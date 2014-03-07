package at.irian.ankor.switching.handler;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface SendHandler<P extends Party> {

    void deliverConnectRequest(Party sender, P receiver, Map<String, Object> connectParameters);

    void deliverEventMessage(Party sender, P receiver, EventMessage message);

    void deliverCloseRequest(Party sender, P receiver);

}
