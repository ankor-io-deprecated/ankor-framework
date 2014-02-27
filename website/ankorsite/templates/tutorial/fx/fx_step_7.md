### Completing the App

There are only a few top-level things missing.

#### Adding the remaining bindings

In our `TaskListController` there are still some UI properties that are not bound to any server variables.
For the sake of simplicity we skipped them in step 3.

    :::java
    @FXML
    public ToggleButton toggleAllButton;
    @FXML
    public Button clearButton;
    @FXML
    public RadioButton filterAll;
    @FXML
    public RadioButton filterActive;
    @FXML
    public RadioButton filterCompleted;

![fx-step-7-1](http://ankor.io/static/images/tutorial/fx-step-7-1.png)

We add the bindings in our root change listener (`myInit`):

    :::java
    toggleAllButton.visibleProperty().bind(footerVisibilityProperty);
    toggleAllButton.selectedProperty().bindBidirectional(modelRef.appendPath("toggleAll").<Boolean>fxProperty());

    clearButton.textProperty().bind(modelRef.appendPath("itemsCompleteText").<String>fxProperty());
    clearButton.visibleProperty().bind(modelRef.appendPath("clearButtonVisibility").<Boolean>fxProperty());

    filterAll.selectedProperty().bindBidirectional(modelRef.appendPath("filterAllSelected").<Boolean>fxProperty());
    filterActive.selectedProperty().bindBidirectional(modelRef.appendPath("filterActiveSelected").<Boolean>fxProperty());
    filterCompleted.selectedProperty().bindBidirectional(modelRef.appendPath("filterCompletedSelected").<Boolean>fxProperty());

#### Changing the filter

There is a clicked methods for each of the filters in the footer.
In order to reload the task list with a different filter the `filter` property needs to be set.
The obvious solution would be to set the property directly.
Due to multi threading reasons this will not work though:

    :::java
    @FXML
    public void filterAllClicked(ActionEvent actionEvent) {
        // This will not work!
        modelRef.appendPath("filter").setValue("all");
    }

Ankor provides a solution for this problem.
The utility class `AnkorPatterns` has a static method `changeValueLater` that will set the value on the correct thread.

    :::java
    @FXML
    public void filterAllClicked(ActionEvent actionEvent) {
        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "all");
    }

Do the same for `filterActiveClicked` and `filterCompletedClicked`,
changing `filter` to `"active"` and `"completed"` respectively.

#### Clear completed todos

The implementation is straight-forward.
When the button is clicked `clearTasks` in our controller is called.
Then we fire an parameterless `Action` to inform the server about the users intent.
The name of the action is `clearTasks` as well.

    :::java
    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fire(new Action("clearTasks"));
    }

#### Toggle all todos

The implementation is straight-forward as well.
The `toggleAll` method has parameter tough.
It's the desired state of all todos, basically the `selectedProperty` of the button that was just clicked.

    :::java
    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        Map<String, Object> params = new HashMap<>();
        params.put("toggleAll", toggleAllButton.selectedProperty().get());
        modelRef.fire(new Action("toggleAll", params));
    }

And that's it. Now we have a basic todo app that is backed by an Ankor server.
If you haven't done so already, check out the [server tutorial][1].
There you will learn how to write an Ankor server that can be used with this app.

[1]: http://ankor.io/tutorials/server