### Rendering Todos

In this step we want to display todos in the UI.

#### tasksList

First of all we need a reference to the UI component `tasksList`, which is a [`VBox`][1].

    :::java
    @FXML
    public VBox tasksList;

![fx-step-5-1](http://ankor.io/static/images/tutorial/fx-step-5-1.png)

In order to add todos to the list we need to add a change listener.
We want to render the list whenever the list of todos changes:

    :::java
    @ChangeListener(pattern = "root.model.(tasks)")
    public void renderTasks(FxRef tasksRef) {
        // ...
    }

We use special syntax here, denoting that we are expecting a reference to the tasks as call parameter.
We do so by surrounding `tasks` in the pattern string with braces, much like you would do in a regular expression.

Here is the body of the method:

    :::java
    tasksList.getChildren().clear(); // 1

    int numTasks = tasksRef.<List>getValue().size(); // 2

    for (int index = 0; index < numTasks; index++) {
        FxRef itemRef = tasksRef.appendIndex(index); // 3
        TaskPane node = new TaskPane(itemRef, index); // 4
        tasksList.getChildren().add(node); // 5
    }

1. First we clear the task list.
2. Next we need the number of tasks in the list. We do so by getting the value of `tasksRef`, which is a list.
However, we are only interested in its length, not the actual list entries.
3. Because we know that `tasksRef` references a list we can call the method `appendIndex` on it.
It's basically the same as `get` for a list, but it returns another `Ref` instead of the list entry itself.
4. For now lets just assume there is already a custom JavaFX component named `TaskPane` that takes a `Ref` and an index.
5. Finally we add the new `TaskPane` to the UI list.

Before you go on, you might want to add this line to your init method (not the constructor):

    :::java
    renderTasks(modelRef.appendPath("tasks"));

This way todos that are already in the list when the application starts will be rendered.

#### Defining a custom JavaFX component

The `TaskPane` is a single todo entry in the list. The markup is already defined in a separate file [`task.fxml`][2].
Open `TaskPane.java` and add the following properties:

    :::java
    private FxRef itemRef;
    private int index;

    @FXML
    public TextField titleTextField;
    @FXML
    public ToggleButton completedButton;
    @FXML
    public Button deleteButton;

![fx-step-5-2](http://ankor.io/static/images/tutorial/fx-step-5-2.png)

The constructor is structured like this:

    :::java
    public TaskPane(FxRef itemRef, int index) {
        this.itemRef = itemRef;
        this.index = index;

        loadFXML();
        addEventListeners();
        bindProperties();
    }

Obviously we need to implement these three methods:

* `loadFXML`: Load the markup that defines the component.
* `addEventListeners`: Leave this unimplemented for now.
* `bindProperties`: Enable two-way bindings.

##### loadFXML

Similar to the `TaskListController` the structure of the `TaskPane` is defined in markup.
The `loadFXML` method loads the markup from the resource folder and sets our `TaskPane` class as its controller.

    :::java
    private void loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

##### bindProperties

    :::java
    private void bindProperties() {
        titleTextField.textProperty().bindBidirectional(itemRef.appendPath("title").<String>fxProperty());
        completedButton.selectedProperty().bindBidirectional(itemRef.appendPath("completed").<Boolean>fxProperty());
        titleTextField.editableProperty().bindBidirectional(itemRef.appendPath("editable").<Boolean>fxProperty());
    }

[1]: http://docs.oracle.com/javafx/2/api/javafx/scene/layout/VBox.html
[2]: https://github.com/ankor-io/ankor-todo-tutorial/blob/fx-step-3/todo-fx/src/main/resources/task.fxml