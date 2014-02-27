### Reacting to Changes

Earlier we've seen how to use the `@ActionListener` annotation to react to `Action`s from the client.
In this step we'll be using the `@ChangeListener` annotation to react to changes form both the client and server.

#### Before we start

It's time to add some additional properties:

    :::java
    private Boolean clearButtonVisibility = false;
    private Integer itemsComplete = 0;
    private String itemsCompleteText;
    private Boolean toggleAll = false;

* `itemsComplete` is the number of todos that have been completed.
* `itemsCompleteText` is the text inside the clear button.
* Since the clear button should not be visible when there are no completed todos, we need a `clearButtonVisibility`.
* `toggleAll` is the active state of the `toggleAll` button.

<div class="alert alert-info">
    <strong>Note:</strong>
    Don't forget to create getters and setters for these properties.
</div>

Let's set the initial state of our view model text properties:

    :::java
    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);
        this.modelRef = modelRef;
        this.taskRepository = taskRepository;

        this.itemsLeftText = itemsLeftText(itemsLeft);
        this.itemsCompleteText = itemsCompleteText(itemsComplete);
    }

The `itemsLeftText` and `itemsCompleteText` helper methods are:

    :::java
    private String itemsLeftText(int itemsLeft) {
        return (itemsLeft == 1) ? "item left" : "items left";
    }

    private String itemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }

#### Methods as Change Listeners

What we want to have is a method that updates the `itemsLeftText` based on the number of tasks:

    :::java
    modelRef.appendPath("itemsLeftText").setValue(itemsLeftText(itemsLeft));

These statements should be called whenever the `itemsLeft` property changes.
We can get this behaviour with a change listener.

A method annotated with the [`@ChangeListener`][1] annotation gets called whenever a certain property changes.
The property is specified by a `pattern`.
Roughly speaking the pattern is the same syntax as if you were accessing the property in JSON.
We will see more advanced patterns later.

In our chase we have `"root.model.itemsLeft"`.
`root` points to the `ModelRoot`, `model` to the `TaskListModel`, and `itemsLeft` to our desired property.

##### Updating itemsLeftText

This will keep `itemsLeftText` in sync with `itemsLeft`.
We will also set `toggleAll`, since it depends on `itemsLeft` as well:

    :::java
    @ChangeListener(pattern = "root.model.itemsLeft")
    public void itemsLeftChanged() {
        modelRef.appendPath("itemsLeftText").setValue(itemsLeftText(itemsLeft));
        modelRef.appendPath("toggleAll").setValue(itemsLeft == 0);
    }

##### Updating the clear button

Another one for the clear button:

    :::java
    @ChangeListener(pattern = "root.model.itemsComplete")
    public void updateClearButton() {
        modelRef.appendPath("clearButtonVisibility").setValue(itemsComplete != 0);
        modelRef.appendPath("itemsCompleteText").setValue(itemsCompleteText(itemsComplete));
    }

##### Changing the footer visibility

We can also listen to multiple patterns:

    :::java
    @ChangeListener(pattern = {
            "root.model.itemsLeft",
            "root.model.itemsComplete"})
    public void updateFooterVisibility() {
        modelRef.appendPath("footerVisibility").setValue(itemsLeft != 0 || itemsComplete != 0);
    }

##### Keeping the item counters updated

As you can see all these listeners depended on `itemsLeft` and `itemsComplete`.
But these properties are currently not consistent with the repository.
To fix this we define a helper method that sets these properties based on the number of entries in the repository.

    :::java
    private void updateItemsCount() {
        modelRef.appendPath("itemsLeft").setValue(taskRepository.fetchActiveTasks().size());
        modelRef.appendPath("itemsComplete").setValue(taskRepository.fetchCompletedTasks().size());
    }

Inside our `newTask` and `deleteTask` methods we can now replace:

    :::java
    int itemsLeft = taskRepository.fetchActiveTasks().size();
    modelRef.appendPath("itemsLeft").setValue(itemsLeft);

with:

    :::java
    updateItemsCount();


[1]: #linkToDocu

