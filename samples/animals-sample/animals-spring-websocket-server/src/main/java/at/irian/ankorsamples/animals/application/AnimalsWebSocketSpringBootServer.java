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

package at.irian.ankorsamples.animals.application;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.GenericApplication;
import at.irian.ankor.system.WebSocketSpringBootServer;
import at.irian.ankorsamples.animals.domain.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot Launcher for the Websocket-based Animals Sample Server.
 */
@ComponentScan("at.irian.ankorsamples.todosample.spring")
public class AnimalsWebSocketSpringBootServer extends WebSocketSpringBootServer {

    private final AnimalRepository animalRepository = new AnimalRepository();

    @Override
    protected Application createApplication() {
        GenericApplication application = new GenericApplication();
        application.setName("Animals Sample (WebSocket Spring Boot)");
        application.setDefaultModelType(ModelRoot.class);
        application.setDefaultModelConstructorArgs(animalRepository);
        return application;
    }

    public static void main(String[] args) {
        SpringApplication.run(AnimalsWebSocketSpringBootServer.class, args);
    }

}
