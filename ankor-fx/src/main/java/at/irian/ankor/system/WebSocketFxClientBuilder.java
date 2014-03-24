package at.irian.ankor.system;

import java.util.HashMap;
import java.util.Map;

/**
* @author Manfred Geiler
*/
public class WebSocketFxClientBuilder {

    private String applicationName;
    private String modelName;
    private Map<String, Object> connectParams = new HashMap<>();
    private String host;
    private int port;
    private String path;
    private WebSocketEndpointListener endpointListener;

    public WebSocketFxClientBuilder withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public WebSocketFxClientBuilder withModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public WebSocketFxClientBuilder withConnectParam(String key, Object value) {
        connectParams.put(key, value);
        return this;
    }

    public WebSocketFxClientBuilder withServer(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
        return this;
    }

    public WebSocketFxClientBuilder withEndpointListener(WebSocketEndpointListener listener) {
        this.endpointListener = listener;
        return this;
    }

    public WebSocketFxClient build() {
        return new WebSocketFxClient(applicationName, modelName, connectParams, host, port, path, endpointListener);
    }
}
