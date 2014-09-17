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

package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.AnkorFXMLLoader;
import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.WebSocketFxClient;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Launcher for the WebSocket-based Animals Sample JavaFX Client.
 */
public class AnimalsWebSocketFxClientStarter extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoWebSocketFxClientStarter.class);

    private AnkorClient ankorClient;

    public static void main(String[] args) {
        launch(args);
    }

    public AnimalsWebSocketFxClientStarter() {
        ankorClient = WebSocketFxClient.builder()
                .withApplicationName("Animals FX Client")
                .withModelName("root")
                //.withConnectParam(CollaborationSingleRootApplication.MODEL_INSTANCE_ID_PARAM, "collaboration-test-instance")
                .withServer("ws://localhost:8080/websocket/ankor")
                .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ankorClient.start();

        stage.setTitle("Ankor Animals FX Sample");

        AnkorFXMLLoader fxmlLoader = new AnkorFXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("main.fxml"));
        fxmlLoader.setResourcesRef(FxRefs.refFactory().ref("root.resources"));
        Pane myPane = (Pane) fxmlLoader.load();

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ankorClient.stop();
        super.stop();
    }

}