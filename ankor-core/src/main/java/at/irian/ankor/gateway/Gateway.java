package at.irian.ankor.gateway;

import at.irian.ankor.gateway.handler.CloseHandler;
import at.irian.ankor.gateway.handler.ConnectHandler;
import at.irian.ankor.gateway.handler.DeliverHandler;
import at.irian.ankor.gateway.msg.GatewayMsg;
import at.irian.ankor.gateway.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface Gateway {

    void registerConnectHandler(ConnectHandler connectHandler);

    void unregisterConnectHandler();

    void registerDisconnectHandler(Class<? extends Party> partyType, CloseHandler<? extends Party> closeHandler);

    void unregisterDisconnectHandler(Class<? extends Party> senderPartyType);

    void registerMessageDeliverer(Class<? extends Party> partyType, DeliverHandler<? extends Party> deliverHandler);

    void unregisterMessageDeliverer(Class<? extends Party> receiverPartyType);


    void routeConnect(Party sender, Map<String, Object> connectParameters);

    void connect(Party sender, Party receiver, Map<String, Object> connectParameters);


    void routeMessage(Party sender, GatewayMsg message);

    void deliverMessage(Party sender, Party receiver, GatewayMsg message);


    void routeClose(Party sender);

    void close(Party sender, Party receiver);


    void start();

    void stop();
}
