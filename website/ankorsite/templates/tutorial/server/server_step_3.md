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

The first thing you will notice are the `@AnkorIgnore` annotations.
These tell Ankor that the annotated properties should not be sent to the clients.

We can't send the entire task repository (our service layer) to the client.
As goes for the `modelRef` that we will use internally.

#### Adding view model properties

Let's add some actual view model properties that Ankor should share with clients:

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
By now it has become bothersome writing them by hand, so make sure your IDE can generate them for you.

For testing purposes we can set some dummy values in the constructor:

    :::java
    footerVisibility = true;
    itemsLeft = 10;
    itemsLeftText = "imaginary items left";

With this in place we are almost ready to connect a client.
However we still need to link our view models with the servlet.

#### Configuring the WebSocket endpoint

We still haven't created a WebSocket endpoint yet.
The [`AnkorEndpoint`][1] extends the WebSocket API's `Endpoint` class and handles all the Ankor related stuff for us.
It starts an Ankor system, accepts new WebSocket connections, assigns Ankor sessions to them and so on.

The only thing that is required of the developer is to overwrite the `getModelRoot` method.
When called, it should return a new instance of our own `ModelRoot` implementation.

The class already exists in the `todo-servlet` module.
Open `TodoEndpoint.java` and implement the `getModelRoot` method.

    :::java
    public class TodoEndpoint extends AnkorEndpoint {
        @Override
        protected Object getModelRoot(Ref rootRef) {
            return new ModelRoot(rootRef, new TaskRepository());
        }
    }

#### Check if the bindings work

Start the servlet by typing `mvn install` in the `ankor-todo` directory.
When the process has finished point your browser to [`http://localhost:8080`](http://localhost:8080).
Your dummy text should appear in the footer.

[1]: #linkToDocu
