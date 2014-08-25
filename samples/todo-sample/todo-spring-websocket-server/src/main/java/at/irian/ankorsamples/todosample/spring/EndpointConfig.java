package at.irian.ankorsamples.todosample.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

@Configuration
public class EndpointConfig extends AnkorSpringConfiguration {

    @Bean
    public ServerEndpointRegistration ankorEndpoint() {
        TodoWebSocketServerEndpoint ep = new TodoWebSocketServerEndpoint();
        return new ServerEndpointRegistration(ep.getPath(), ep);
    }
}
