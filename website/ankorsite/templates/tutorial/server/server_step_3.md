### Sharing Properties

#### Creating another view model

Let's start off by turning the `TaskListModel` into a view model.
This is similar to the root model.
We do so by calling `initViewModel` inside the constructor.

    :::java
    public class TaskListModel {

        @AnkorIgnore
        private final Ref modelRef;

        @AnkorIgnore
        private final TaskRepository taskRepository;

        public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
            AnkorPatterns.initViewModel(this, modelRef);

            this.modelRef = modelRef;
            this.taskRepository = taskRepository;
        }
    }

The first thing you will notice are the [`@AnkorIgnore`][1] annotations.
These annotations tell Ankor that the annotated properties should not be included in the view model.
This means that they will not be sent to the client.
Obviously we can't send the entire task repository (our service layer) to the client.
As goes for the `modelRef` which we only use internally.

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

#### Configuring the WebSocket endpoint

We still haven't created a WebSocket endpoint yet.
The [`AnkorEndpoint`][2] extends the WebSocket API's [`Endpoint`][3] class and handles the Ankor related things for us:
It starts an Ankor system, accepts new WebSocket connections and assigns Ankor sessions to them.

The only thing that is required of the developer is to overwrite the `getModelRoot` method.
When called it should return a new instance of our `ModelRoot` implementation.

The class already exists in the `todo-servlet` module.
Open `TodoEndpoint.java` and implement the `getModelRoot` method:

    :::java
    public class TodoEndpoint extends AnkorEndpoint {
        @Override
        protected Object getModelRoot(Ref rootRef) {
            return new ModelRoot(rootRef, new TaskRepository());
        }
    }

#### Check if the bindings work

Start the servlet again by typing `mvn install` in the root directory. 
When the process has finished point your browser to [`http://localhost:8080`](http://localhost:8080).
Your dummy text should appear in the footer.

[1]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/messaging/AnkorIgnore.html
[2]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/servlet/websocket/AnkorEndpoint.html
[3]: http://docs.oracle.com/javaee/7/api/javax/websocket/Endpoint.html
