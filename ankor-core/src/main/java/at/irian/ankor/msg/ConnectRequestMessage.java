package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ConnectRequestMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectMessage.class);

    private final String modelName;
    private final Map<String,Object> connectParameters;

    public ConnectRequestMessage(Party sender, String modelName, Map<String, Object> connectParameters) {
        super(sender);
        this.modelName = modelName;
        this.connectParameters = connectParameters;
    }

    public String getModelName() {
        return modelName;
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
        ((Listener)listener).onConnectRequest(this);
    }

    public interface Listener extends MessageListener {
        void onConnectRequest(ConnectRequestMessage msg);
    }

}

