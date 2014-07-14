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

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.switching.connector.websocket.WebSocketEndpoint;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public abstract class WebSocketServerEndpoint extends WebSocketEndpoint implements ServerApplicationConfig {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketServerEndpoint.class);

    private static final Map<Class<? extends WebSocketServerEndpoint>, AnkorSystem> SYSTEM_MAP
            = new ConcurrentHashMap<Class<? extends WebSocketServerEndpoint>, AnkorSystem>();

    /**
     * Gets the AnkorSystem for this endpoint class.
     * If there is no AnkorSystem yet running, it is created by a call to {@link #createAnkorSystem()}.
     *
     * @return AnkorSystem for this endpoint class.
     */
    @Override
    protected AnkorSystem getAnkorSystem() {
        Class<? extends WebSocketServerEndpoint> endpointClass = this.getClass();
        AnkorSystem ankorSystem = SYSTEM_MAP.get(endpointClass);
        if (ankorSystem == null) {
            synchronized (SYSTEM_MAP) {
                ankorSystem = SYSTEM_MAP.get(endpointClass);
                if (ankorSystem == null) {
                    ankorSystem = createAnkorSystem();
                    SYSTEM_MAP.put(endpointClass, ankorSystem);
                    ankorSystem.start();
                }
            }
        }
        return ankorSystem;
    }

    protected AnkorSystem createAnkorSystem() {
        return createAnkorSystemBuilder().createServer();
    }

    protected AnkorSystemBuilder createAnkorSystemBuilder() {
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        Application application = createApplication();
        return new AnkorSystemBuilder()
                .withName(application.getName() + " - WebSocket Server")
                .withApplication(application)
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", false)
                .withConfigValue("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled", true);
    }

    @Override
    public final Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return Collections.singleton(ServerEndpointConfig.Builder.create(this.getClass(),
                                                                         this.getPath())
                                                                 .build());
    }

    @Override
    public final Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return Collections.emptySet();
    }

    protected abstract Application createApplication();


}
