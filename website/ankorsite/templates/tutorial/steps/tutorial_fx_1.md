### Starting the Application

When creating a Ankor JavaFX application you have two choices: You can either extend the `AnkorApplication` class
which starts the Ankor system for you, or you can set up your Ankor system manually.
Both work the same way and the rest of the tutorial does not depend on
your choice here. However, chose the first option if you want to get going fast.

Open [`App.java`][1] and add either of the the following lines:

<ul class="nav nav-tabs">
    <li class="active" ><a href="#tab11" data-toggle="tab">AnkorApplication</a></li>
    <li><a href="#tab12" data-toggle="tab">javafx.application.Application</a></li>
</ul>

<div class="tab-content">
<div class="tab-pane active" id="tab11">
<pre><code>public class App extends AnkorApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void startFXClient(Stage stage) throws Exception {
        stage.setTitle("Ankor Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    @Override
    protected String getWebSocketUri() {
        return "wss://ankor-todo-sample.irian.at/websocket/ankor";
    }
}</pre></code>
<p>The <code>AnkorApplication</code> class does all the plumbing and wiring for you. When entering the
<code>startFXClient</code> method a WebSocket connection has already been set up, the Ankor system
has received an id from the server and heartbeat messages are sent to the server in fixed intervals.</p>

</div>

<div class="tab-pane" id="tab12">
<p>This is an alternative implementation of the <code>App</code> class providing the same functionality:</p>

<pre><code>public class App extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnkorSystemBuilder appBuilder = new AnkorSystemBuilder()
                .withMessageBus(new WebSocketMessageBus(new ViewModelJsonMessageMapper()))
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory());

        AnkorSystem app = appBuilder.createWebSocketClient("wss://ankor-todo-sample.irian.at/websocket/ankor").start();

        refFactory = (FxRefFactory) ((SingletonSessionManager) app.getSessionManager())
                    .getSession().getRefContext().refFactory();

        stage.setTitle("Ankor Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    private static FxRefFactory refFactory;

    // we will need access to the refFactory later
    public static FxRefFactory refFactory() {
        return refFactory;
    }
}</code></pre>
</div>
</div>

For now we are connecting to an existing Ankor server at `wss://ankor-todo-sample.irian.at/websocket/ankor`.
The test sever will be able to understand and process the messages that our todo application is going to send.
This will give you the experience of an application developer adding a new client
platform to an existing Ankor system.
However, if you want to write your own server first you can do so by [heading over to the respective step](#).

You can now start the app and check if it throws any exceptions.

    // TODO maven command to start application

The window should look exactly like this. As you can see the UI structure has already been defined and
styled for you. Building an JavaFX app from ground up is outside the scope of this tutorial.</p>

![fx-step-1-1](/static/images/tutorial/fx-step-1-1.png)

If the app doesn't start the test server could be offline.
You can check the online status via
[websocket.org](http://www.websocket.org/echo.html). The WebSocket url is `wss://ankor-todo-sample.irian.at/websocket/ankor`.
A running Ankor server should return an UUID upon connecting.

Note that you can always [write your own server](#). 

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-1/todo-javafx-client/src/main/java/io/ankor/tutorial/App.java
