package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.servlet.websocket.AnkorEndpoint;
import at.irian.ankor.servlet.websocket.AnkorWebSocketConfig;

import javax.servlet.annotation.WebListener;

@WebListener
public class TodoWebSocketConfig extends AnkorWebSocketConfig {
    @Override
    protected Class<? extends AnkorEndpoint> webSocketEndpointClass() {
        return TodoEndpoint.class;
    }
}
