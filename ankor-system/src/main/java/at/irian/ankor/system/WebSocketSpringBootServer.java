/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.system;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Base class for Websocket-based Spring Boot Standalone Server.
 * <p>
 * Example implementation:
 * <pre>
 *     @<span/>Override
 *     protected Application createApplication() {
 *          return new MyAnkorApplication();
 *     }
 *
 *     public static void main(String[] args) {
 *          SpringApplication.run(MyWebSocketSpringBootServer.class, args);
 *     }
 * </pre>
 * </p>
 */
@EnableAutoConfiguration
public abstract class WebSocketSpringBootServer extends WebSocketServerEndpoint
                                                implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        // serve static resources in utf-8 encoding. May not be necessary for your app.
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }

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
    public ServerEndpointRegistration ankorEndpoint() {
        return new ServerEndpointRegistration(this.getPath(), this);
    }

    @Override
    public String getPath() {
        return "/websocket/ankor/{clientId}";
    }

}
