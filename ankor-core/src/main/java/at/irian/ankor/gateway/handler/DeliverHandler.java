package at.irian.ankor.gateway.handler;

import at.irian.ankor.gateway.msg.GatewayMsg;
import at.irian.ankor.gateway.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface DeliverHandler<P extends Party> {

    void open(Party sender, P receiver, Map<String, Object> connectParameters);

    void deliver(Party sender, P receiver, GatewayMsg message);

    void close(Party sender, P receiver);

}
