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
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

/**
 * @author Manfred Geiler
 */
public class SocketStandaloneServer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketStandaloneServer.class);

    protected final Application application;
    private Thread thread;
    private volatile boolean running;

    public SocketStandaloneServer(Application application) {
        this.application = application;
    }

    public void start() {
        thread = createMainThread();
        running = true;
        thread.start();
        sleepForever();
    }

    protected Thread createMainThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createAnkorSystem()
                        .start();
            }
        });
        thread.setDaemon(false);
        thread.setName(application.getName() + " main server thread");
        return thread;
    }

    protected AnkorSystem createAnkorSystem() {
        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();
        return new AnkorSystemBuilder()
                .withApplication(application)
                .withActorSystemEnabled()
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", true)
                .createServer();
    }

    protected void sleepForever() {
        boolean interrupted = false;
        while (running && !interrupted) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        Thread.currentThread().interrupt();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }
}
