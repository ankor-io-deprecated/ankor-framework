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

package at.irian.ankorsamples.todosample.spring;

import at.irian.ankor.application.Application;
import at.irian.ankor.spring.SpringBasedAnkorApplication;
import at.irian.ankor.system.WebSocketSpringBootServer;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
@ComponentScan("at.irian.ankorsamples.todosample.spring")
public class TodoWebSocketSpringBootServer extends WebSocketSpringBootServer {

    @Bean
    public Application application() {
        SpringBasedAnkorApplication application = new SpringBasedAnkorApplication();
        application.setName("Todo (WebSocket Spring Boot)");
        application.setDefaultModelType(SpringModelRoot.class);
        return application;
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoWebSocketSpringBootServer.class, args);
    }

}
