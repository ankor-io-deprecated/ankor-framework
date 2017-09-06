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

import at.irian.ankor.viewmodel.factory.AbstractBeanFactory;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.factory.SpringBasedBeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
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

/**
 * Websocket-based Spring Boot Standalone Server.
 * The path "at.irian.ankor.spring" is scanned for Spring beans. So the
 * {@link at.irian.ankor.viewmodel.factory.SpringBasedBeanFactory} is automatically getting found.
 * Application implementors have to provide a Spring bean of type {@link at.irian.ankor.application.Application}.
 * <p>
 * Ankor Spring Boot Applications are typically started in a main method like so:
 * <pre>
 *
 *     @Bean
 *     public final Application application() {
 *         SpringBasedAnkorApplication application = new SpringBasedAnkorApplication();
 *         application.setDefaultModelType(MyRootModel.class);
 *         return application;
 *     }
 *
 *     public static void main(String[] args) {
 *          SpringApplication.run(WebSocketSpringBootServer.class, args);
 *     }
 * </pre>
 * </p>
 */
@EnableAutoConfiguration
public abstract class WebSocketSpringBootServer extends WebSocketServerEndpoint
                                                implements EmbeddedServletContainerCustomizer {

    private ApplicationContext springApplicationContext;

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        // serve static resources in utf-8 encoding. May not be necessary for your app.
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }

    @Bean
    public ServletContextAware endpointExporterInitializer(final ApplicationContext applicationContext) {
        this.springApplicationContext = applicationContext;
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
                serverEndpointExporter.afterSingletonsInstantiated();
            }
        };
    }

    @Bean
    public ServerEndpointRegistration ankorEndpoint() {
        return new ServerEndpointRegistration(this.getPath(), this);
    }

    @Override
    protected AnkorSystemBuilder createAnkorSystemBuilder() {
        AnkorSystemBuilder ankorSystemBuilder = super.createAnkorSystemBuilder();
        BeanMetadataProvider beanMetadataProvider = ankorSystemBuilder.getBeanMetadataProvider();
        BeanFactory baseBeanFactory = ankorSystemBuilder.getBeanFactory();
        SpringBasedBeanFactory beanFactory = new SpringBasedBeanFactory(beanMetadataProvider,
                                                                        (AbstractBeanFactory)baseBeanFactory,
                                                                        springApplicationContext);
        return ankorSystemBuilder.withBeanFactory(beanFactory);
    }

    @Override
    protected String getPath() {
        return "/websocket/ankor/{clientId}";
    }

}
