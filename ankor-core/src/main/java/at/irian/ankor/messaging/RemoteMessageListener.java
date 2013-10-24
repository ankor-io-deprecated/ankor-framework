package at.irian.ankor.messaging;

/**
 * A MessageListener that listens for remote action and change messages from a remote system.
 *
 * @author Manfred Geiler
 */
public interface RemoteMessageListener extends ActionMessage.Listener, ChangeMessage.Listener {
}
