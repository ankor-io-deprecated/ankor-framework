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
                    .withServer("ws://localhost:8080/websocket/ankor")
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

This assumes that you have an Ankor server with WebSocket endpoint running on your local machine at port 8080.
If you've already done the [Server Tutorial][servertutorial] you can use that one you implemented yourself.

Otherwise you can use the one provided with this repository.
You can start it by invoking `mvn spring-boot:run` in the `todo-servlet` directory.

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

[1]: http://ankor.io/javadoc/at/irian/ankor/system/WebSocketFxClient.html
[servertutorial]: http://ankor.io/tutorials/server
[3]: http://docs.oracle.com/javafx/2/api/javafx/application/Application.html
