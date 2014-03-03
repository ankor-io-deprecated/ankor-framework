package at.irian.ankor.connector.socket;

import at.irian.ankor.action.Action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketConnectAction extends Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnectAction.class);

    public static final String ACTION_NAME = "$socketConnect";

    private SocketConnectAction(Map<String, Object> params) {
        super(ACTION_NAME, params);
    }

    public static Action create(String hostname, int port) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hostname", hostname);
        params.put("port", port);
        return new SocketConnectAction(params);
    }

}
