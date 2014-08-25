package hello;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

import javax.servlet.ServletContext;
import java.util.Map;

// http://stackoverflow.com/a/25425384/870615
public abstract class AnkorSpringConfiguration {
    
    @Bean
    public ServletContextAware endpointExporterInitializer(final ApplicationContext applicationContext) {
        return new ServletContextAware() {
            @Override
            public void setServletContext(ServletContext servletContext) {
                ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
                serverEndpointExporter.setApplicationContext(applicationContext);
                registerAllServerEndpoints(serverEndpointExporter);

                try {
                    serverEndpointExporter.afterPropertiesSet();
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }

            private void registerAllServerEndpoints(ServerEndpointExporter serverEndpointExporter) {
                Map<String, ServerEndpointRegistration> serverEndpointRegistrations =
                        applicationContext.getBeansOfType(ServerEndpointRegistration.class);

                for (String name : serverEndpointRegistrations.keySet()) {
                    serverEndpointExporter.postProcessAfterInitialization(serverEndpointRegistrations.get(name), name);
                }
            }
        };
    }
    
    @Bean
    public abstract ServerEndpointRegistration ankorEndpoint();
}
