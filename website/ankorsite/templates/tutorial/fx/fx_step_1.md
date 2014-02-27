### Starting the App

#### The AnkorApplication class

When creating a Ankor JavaFX application you have two choices: You can either extend the `AnkorApplication` class
which starts the Ankor system for you, or you can set up your own Ankor system manually.
Both work the same way under the hood and the rest of the tutorial does not depend on
your choice here. However, choose the first option if you want to start quickly.

The fastest way to create a Ankor JavaFX app is to extend the [`AnkorApplication`][1] class.
This class itself is a subtype of the JavaFX [`Application`][3] class.

Open `App.java` and add the following lines:

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

However, if you want to write your own server first you can learn how to do so in the [server tutorial][2].
If you want to run your own server locally [you can do so as well][2].

#### Adding JavaFX to the classpath

Now go to the `todo-fx` folder in the project.

    cd todo-fx

Before we can start the application we need to add JavaFX to the classpath.
While JDK 7 ships with JavaFX it is not added to the classpath by default.
The following command fixes this:

    mvn jfx:fix-classpath

<div class="alert alert-info">
    <strong>Note:</strong>
    You have to run this command as super user / with administrator privileges.
</div>

#### Starting the application

You can now start the app and check if it throws any exceptions.

    mvn jfx:run

The window should look exactly like the one below. As you can see the UI structure has already been defined and
styled for you. Building an JavaFX app from ground up is outside the scope of this tutorial.</p>

![fx-step-1-1](http://ankor.io/static/images/tutorial/fx-step-1-1.png)

If the app doesn't start the test server could be offline.
You can check the online status via
[websocket.org](http://www.websocket.org/echo.html) (the WebSocket url is `wss://ankor-todo-sample.irian.at/websocket/ankor`).
A running Ankor server should send an UUID when a connection is established.

If the server appears to be offline you can still [run your own server][2].

[1]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/fx/websocket/AnkorApplication.html
[2]: http://ankor.io/tutorials/server/1
[3]: http://docs.oracle.com/javafx/2/api/javafx/application/Application.html
