### Starting the Application

When creating a Ankor JavaFX application you have two choices: You can either extend the `AnkorApplication` class
which starts the Ankor system for you, or you can set up your own Ankor system manually.
Both work the same way under the hood and the rest of the tutorial does not depend on
your choice here. However, choose the first option if you want to start quickly.

The fastest way to create a Ankor JavaFX app is to extend the `AnkorApplication` class. This

Open [`App.java`][1] and add the following lines:

    :::java
    public class App extends AnkorApplication {

        // This is to start the JavaFX application.
        public static void main(String[] args) {
            launch(args);
        }

        // This method gets called after a connection has been established
        @Override
        protected void startFXClient(Stage stage) throws Exception {
            stage.setTitle("Ankor Todo Sample");

            // predefined fxml
            Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

            Scene myScene = new Scene(myPane);

            // predefined styles
            myScene.getStylesheets().add("style.css");

            stage.setScene(myScene);
            stage.show();
        }

        // The WebSocket endpoint to connect to
        @Override
        protected String getWebSocketUri() {
            return "wss://ankor-todo-sample.irian.at/websocket/ankor";
        }
    }

The abstract `AnkorApplication` class does all the plumbing and wiring for you: When entering the
`startFXClient` method a WebSocket connection has been set up, the Ankor system
has received an id from the server and heartbeat messages are being sent to the server in fixed intervals.

For now we are connecting to an existing Ankor server at `wss://ankor-todo-sample.irian.at/websocket/ankor`.
This server will be able to understand and process the messages that our todo application is going to send.
This will give you the experience of an application developer adding a new client platform to an existing Ankor system.
However, if you want to write your own server first you can learn how to do so in the [server section][2].

You can now start the app and check if it throws any exceptions.

    // TODO maven command to start application

The window should look exactly like the one below. As you can see the UI structure has already been defined and
styled for you. Building an JavaFX app from ground up is outside the scope of this tutorial.</p>

![fx-step-1-1](/static/images/tutorial/fx-step-1-1.png)

If the app doesn't start the test server could be offline.
You can check the online status via
[websocket.org](http://www.websocket.org/echo.html) (the WebSocket url is `wss://ankor-todo-sample.irian.at/websocket/ankor`).
A running Ankor server should send an UUID when a connection is established.

If the server appears to be offline you can still [write your own server][2].

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-1/todo-javafx-client/src/main/java/io/ankor/tutorial/App.java
[2]: /tutorials/server/0