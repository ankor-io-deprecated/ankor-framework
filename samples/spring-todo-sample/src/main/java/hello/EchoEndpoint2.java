package hello;

import javax.websocket.*;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public class EchoEndpoint2 extends Endpoint {
    
    @Override
    public void onOpen(final Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                try {
                    session.getBasicRemote().sendText("echo 2: " + s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
