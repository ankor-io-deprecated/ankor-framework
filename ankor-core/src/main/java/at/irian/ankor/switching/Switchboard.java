package at.irian.ankor.switching;

import at.irian.ankor.switching.handler.CloseHandler;
import at.irian.ankor.switching.handler.OpenHandler;
import at.irian.ankor.switching.handler.SendHandler;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface Switchboard {

    void registerOpenHandler(OpenHandler openHandler);

    void unregisterOpenHandler();

    void registerCloseHandler(Class<? extends Party> partyType, CloseHandler<? extends Party> closeHandler);

    void unregisterCloseHandler(Class<? extends Party> senderPartyType);

    void registerSendHandler(Class<? extends Party> partyType, SendHandler<? extends Party> sendHandler);

    void unregisterSendHandler(Class<? extends Party> receiverPartyType);


    void open(Party sender, Map<String, Object> connectParameters);

    //void connect(Party sender, Party receiver, Map<String, Object> connectParameters);


    void send(Party sender, EventMessage message);

    void send(Party sender, Party receiver, EventMessage message);


    void close(Party sender);

    //void close(Party sender, Party receiver);


    void start();

    void stop();
}
