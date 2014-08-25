package hello;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/echo"/*, configurator = SpringConfigurator.class*/)
public class EchoEndpoint {

    @OnMessage
    public void handleMessage(Session session, String message) throws IOException {
        session.getBasicRemote().sendText("echo: " + message);
    }

}
