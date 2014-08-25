package hello;

import at.irian.ankor.switching.connector.websocket.WebSocketEndpoint;
import at.irian.ankorsamples.todosample.servlet.TodoWebSocketServerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

@Configuration
public class EndpointConfig extends AnkorSpringConfiguration {

    @Bean
    public ServerEndpointRegistration ankorEndpoint() {
        WebSocketEndpoint ep = new TodoWebSocketServerEndpoint();
        return new ServerEndpointRegistration(ep.getPath(), ep);
    }
}
