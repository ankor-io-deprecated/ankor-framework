package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;
import at.irian.ankor.msg.party.SystemParty;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class DefaultDisconnectMessageListener implements DisconnectMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultDisconnectMessageListener.class);

    private final SwitchingCenter switchingCenter;
    private final MessageBus messageBus;

    public DefaultDisconnectMessageListener(SwitchingCenter switchingCenter, MessageBus messageBus) {
        this.switchingCenter = switchingCenter;
        this.messageBus = messageBus;
    }

    @Override
    public void onDisconnectMessage(DisconnectMessage msg) {
        Party sender = msg.getSender();
        LOG.info("Disconnect message received from {}", sender);

        Collection<Party> receivers = switchingCenter.getConnectedParties(msg.getSender());

        switchingCenter.disconnectAll(msg.getSender());

        for (Party receiver : receivers) {
            if (!switchingCenter.hasConnectedParties(receiver)) {
                messageBus.broadcast(new CloseMessage(SystemParty.getInstance(), receiver));
            }
        }

    }
}
