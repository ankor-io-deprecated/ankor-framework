### Rendering Todos

In this step we want to display todos in the UI.

#### Top Level

First of all we need a reference to the UI list, which is actually a [`VBox`][1].

    :::java
    @FXML
    public VBox tasksList;

In order to add todos to the list we need to add a change listener.
We want to render the list whenever the list of todos changes:

    :::java
    @ChangeListener(pattern = "root.model.(tasks)")
    public void renderTasks(FxRef tasksRef) {
        // TODO
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

#### Defining a Custom JavaFX Component

The `TaskPane` is a single todo entry in the list. The markup is already defined in a separate file [`task.fxml`][2].

Open `TaskPane.java`. We will need the following properties:

    :::java
    private FxRef itemRef;
    private int index;

    @FXML
    public TextField titleTextField;
    @FXML
    public ToggleButton completedButton;
    @FXML
    public Button deleteButton;

The constructor is structured like this:

    public TaskPane(FxRef itemRef, int index) {
        this.itemRef = itemRef;
        this.index = index;

        loadFXML();
        addEventListeners();
        setValues();
        bindProperties();
    }

We need to implement these four methods:

* `loadFXML`: Load the markup that defines the component.
* `addEventListeners`: Leave this unimplemented for now.
* `setValues`: Set the initial values from the `itemRef`.
* `bindProperties`: Enable two-way bindings.

##### loadFXML

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

##### setValues

    private void setValues() {
        titleTextField.textProperty().setValue(itemRef.appendPath("title").<String>getValue());
        completedButton.selectedProperty().setValue(itemRef.appendPath("completed").<Boolean>getValue());
        titleTextField.editableProperty().setValue(itemRef.appendPath("editable").<Boolean>getValue());
    }

##### bindProperties

    private void bindProperties() {
        titleTextField.textProperty().bindBidirectional(itemRef.appendPath("title").<String>fxProperty());
        completedButton.selectedProperty().bindBidirectional(itemRef.appendPath("completed").<Boolean>fxProperty());
        titleTextField.editableProperty().bindBidirectional(itemRef.appendPath("editable").<Boolean>fxProperty());
    }

[1]: http://docs.oracle.com/javafx/2/api/javafx/scene/layout/VBox.html
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-5/todo-fx/src/main/resources/task.fxml
