package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageListener {
    void onActionMessage(ActionMessage message);
    void onChangeMessage(ChangeMessage message);
}
