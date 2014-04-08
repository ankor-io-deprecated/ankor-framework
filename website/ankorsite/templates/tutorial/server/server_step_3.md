### Sharing Properties

#### Creating another view model

Let's start off by turning the `TaskListModel` into a view model.
This is similar to the root model.
We do so by calling `initViewModel` inside the constructor.

    :::java
    public class TaskListModel {

        private final Ref modelRef;

        private final TaskRepository taskRepository;

        public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
            AnkorPatterns.initViewModel(this, modelRef);

            this.modelRef = modelRef;
            this.taskRepository = taskRepository;
        }
    }

The first thing you might notice are the two fields 'modelRef' and 'taskRepository'. These are both objects
we need for implementing our server behaviour, but we do not want to send them to the client.
Obviously we can't send the entire task repository (our service layer) to the client.
As goes for the `modelRef` which we only use internally.
To accomplish this we just omit (public) getters for these fields because Ankor (by default) does only send
properties with public getters to the client.

#### Adding view model properties

Let's add some view model properties that Ankor should not ignore:

    :::java
    private Boolean footerVisibility = false;
    private Integer itemsLeft = 0;
    private String itemsLeftText;

    public Boolean getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(Boolean footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public Integer getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(Integer itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public String getItemsLeftText() {
        return itemsLeftText;
    }

    public void setItemsLeftText(String itemsLeftText) {
        this.itemsLeftText = itemsLeftText;
    }

Again we need getters and setters for all of these.

For testing purposes we should set some dummy values in the constructor:

    :::java
    footerVisibility = true;
    itemsLeft = 10;
    itemsLeftText = "imaginary items left";

With this we are almost ready to test our server implementation.
But we still need to link our view models with the servlet.

### The Ankor Application singleton

Until now Ankor does not know anything about our view model. So there is some glue code we must provide.
We need to tell Ankor some things about our model. We do this by implementing the Ankor 'Application' interface.
Let's open the already existing class 'TodoApplication' in the 'todo-server' module.

    :::java
    public class TodoApplication implements at.irian.ankor.application.Application {

        @Override
        public String getName() {
            // tell Ankor the name of our application
            return "Todo Server Application";
        }

        //...
    }

We also provide the creation logic and a factory method for our model:

    :::java
    @Override
    public boolean supportsModel(String modelName) {
        // our application supports exactly one model named 'root'
        return "root".equals(modelName);
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectParameters) {
        // we do not support connecting different clients (or users) to the same model
        // therefore we just return null here - meaning: we did not find a proper already existing model
        return null;
    }

    private final TaskRepository taskRepository = new TaskRepository();

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        // create a new instance of our model root
        Ref rootRef = refContext.refFactory().ref("root");
        return new ModelRoot(rootRef, taskRepository);
    }

And some obligatory cleanup stuff:

    :::java
    @Override
    public void releaseModel(String modelName, Object modelRoot) {
        // nothing to do here because our model instance did not allocate any resources
    }

    @Override
    public void shutdown() {
        // nothing to do here because our Application singleton did not allocate any resources
    }

We now finished our basic Ankor setup, let's see how we can start it.

#### Configuring the WebSocket endpoint

We still haven't created a WebSocket endpoint yet.
The [`WebSocketServerEndpoint`][1] extends the WebSocket API's [`Endpoint`][2] class and handles the Ankor related
things for us:
It starts an Ankor system, accepts new WebSocket connections and assigns Ankor sessions to them.

There are two things we must implement in our application specific Endpoint class.

The class already exists in the `todo-servlet` module.
Open `TodoEndpoint.java` and implement the `createApplication` method in which we create our Ankor Application singleton
instance.

    :::java
    public class TodoEndpoint extends WebSocketServerEndpoint {
        @Override
        protected Application createApplication() {
            return new TodoApplication();
        }

        // ...
    }

We also must provide the WebSocket URL path.

    :::java
    public class TodoEndpoint extends WebSocketServerEndpoint {

        // ...

        @Override
        protected String getPath() {
            return "/websocket/ankor/{clientId}";
        }
    }

We should now have a runnable WebSockets Ankor application.

#### Check if the bindings work

Start the servlet again by typing `mvn install` in the root directory. 
When the process has finished point your browser to [`http://localhost:8080`](http://localhost:8080).
Your dummy text should appear in the footer.

[1]: http://ankor.io/static/javadoc/apidocs-0.2/at/irian/ankor/system/WebSocketServerEndpoint.html
[2]: http://docs.oracle.com/javaee/7/api/javax/websocket/Endpoint.html
