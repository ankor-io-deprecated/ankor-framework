package at.irian.ankor.switching.handler;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface SendHandler<P extends Party> {

    void sendConnectRequest(Party sender, P receiver, Map<String, Object> connectParameters);

    void sendEventMessage(Party sender, P receiver, EventMessage message);

    void sendCloseRequest(Party sender, P receiver);

}
