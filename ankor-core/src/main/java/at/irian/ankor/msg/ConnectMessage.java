package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ConnectMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectMessage.class);

    private final Map<String,Object> connectParameters;

    public ConnectMessage(Party sender, Map<String, Object> connectParameters) {
        super(sender);
        this.connectParameters = connectParameters;
    }

    public Map<String, Object> getConnectParameters() {
        return connectParameters;
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onConnectMessage(this);
    }

    public interface Listener extends MessageListener {
        void onConnectMessage(ConnectMessage msg);
    }

}

