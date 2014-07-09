### Starting the App

#### Using a AnkorClient

A Ankor JavaFX application is just a regular JavaFX application. 
It extends the [`Application`][3] class, calls `launch` in the main method and has `start` and `stop` methods.

Ankor comes into play in the constructor. 
To enable Ankor the application needs a `AnkorClient` property. 
There are several implementations for various use cases. 
In our case we want a [`WebSocketFxClient`][1].
This is a `AnkorClient` implementation for JavaFX that connects to a WebSocket endpoint.

Open `App.java` and add the following lines:

    :::java
    public class App extends Application {
        private AnkorClient client;
    
        public static void main(String[] args) {
            launch(args);
        }
    
        public App() throws Exception {
            client = WebSocketFxClient.builder()
                    .withApplicationName("Todo FX Client")
                    .withModelName("root")
                    .withConnectParam("todoListId", "collaborationTest")
                    .withServer("wss://ankor-todo-sample.irian.at/websocket/ankor")
                    .build();
        }
    
        @Override
        public void start(Stage stage) throws Exception {
            client.start();
    
            stage.setTitle("Ankor JavaFX Todo Sample");
            Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));
    
            Scene myScene = new Scene(myPane);
            myScene.getStylesheets().add("style.css");
    
            stage.setScene(myScene);
            stage.show();
        }
    
        @Override
        public void stop() throws Exception {
            client.stop();
            super.stop();
        }
    }

For now we are connecting to an existing Ankor server at `wss://ankor-todo-sample.irian.at/websocket/ankor`.
This server will be able to understand and process the messages that our todo application is going to send.
This will give you the experience of an application developer adding a new client platform to an existing Ankor system.

However, if you want to write your own server first you can learn how to do so in the [server tutorial][2].
If you want to run your own server locally [you can do so as well][2].

#### Adding JavaFX to the classpath

<div class="alert alert-info">
    <strong>Note:</strong>
    This step is not required if you have Java 8 installed.
</div>

Now go to the `todo-fx` folder in the project.

    cd todo-fx

Before we can start the application we need to add JavaFX to the classpath.
While JDK 7 ships with JavaFX it is not added to the classpath by default.
The following command will fix this:

    mvn jfx:fix-classpath

<div class="alert alert-info">
    <strong>Note:</strong>
    You have to run this command as super user / with administrator privileges.
</div>

#### Starting the application

You can now start the app and check if it throws any exceptions.

    mvn jfx:run

The window should look exactly like the one below. As you can see the UI structure has already been defined and
styled for you. Building an JavaFX app from ground up is outside the scope of this tutorial.

![fx-step-1-1](http://ankor.io/static/images/tutorial/fx-step-1-1.png)

If the app doesn't start the test server could be offline.
You can check the online status via
[websocket.org](http://www.websocket.org/echo.html) (the WebSocket url is `wss://ankor-todo-sample.irian.at/websocket/ankor`).
A running Ankor server should send an UUID when a connection is established.

If the server appears to be offline you can still [run your own server][2].

[1]: http://ankor.io/static/javadoc/apidocs-0.3/at/irian/ankor/system/WebSocketFxClient.html
[2]: http://ankor.io/tutorials/server/1
[3]: http://docs.oracle.com/javafx/2/api/javafx/application/Application.html
