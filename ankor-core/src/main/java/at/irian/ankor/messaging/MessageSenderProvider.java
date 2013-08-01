package at.irian.ankor.messaging;

import at.irian.ankor.session.RemoteSystem;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public interface MessageSenderProvider {
    Collection<? extends RemoteSystem> getKnownRemoteSystems();
    MessageSender getMessageSenderFor(RemoteSystem remoteSystem);
}
