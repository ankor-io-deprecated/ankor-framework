package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.party.Party;

/**
 * @author Manfred Geiler
 */
public interface ConnectorPlug {

    void registerConnectionHandler(Class<? extends Party> receiverPartyType,
                                   ConnectionHandler<? extends Party> connectionHandler);

    void unregisterConnectionHandler(Class<? extends Party> receiverPartyType);

    void registerTransmissionHandler(Class<? extends Party> receiverPartyType,
                                     TransmissionHandler<? extends Party> transmissionHandler);

    void unregisterTransmissionHandler(Class<? extends Party> receiverPartyType);

}
