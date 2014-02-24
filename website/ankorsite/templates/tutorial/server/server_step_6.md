### Reacting to Changes

Earlier we've seen how to use the `@ActionListener` annotation to react to `Action`s from the client.
In this step we'll be using the `@ChangeListener` annotation to react to changes form both the client and server.

#### Before we start

First we add some additional properties:

    :::java
    private Integer itemsComplete = 0;
    private String itemsCompleteText = "";
    private Boolean clearButtonVisibility = false;
    private Boolean toggleAll = false;
    
* `itemsComplete` is the number of todos that have been completed.
* `itemsCompleteText` is the text inside the clear button.
* Since the clear button should not be visible when there are no completed todos, we need a `clearButtonVisibility`.
* `toggleAll` is the active state of the `toggleAll` button.

<div class="alert alert-info">
    <strong>Note:</strong>
    Don't forget to create getters and setters for these properties.
</div>

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

We will also set `toggleAll` to the correct value:

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
    
    private String itemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }
    
##### Changing the footer visibility 

We can also listen to multiple patterns: 

    :::java
    @ChangeListener(pattern = {
            "root.model.itemsLeft",
            "root.model.itemsComplete"})
    public void updateFooterVisibility() {
        modelRef.appendPath("footerVisibility").setValue(taskRepository.getTasks().size() != 0);
    }
    
##### Keeping the item counters updated
    
Until now `itemsLeft` and `itemsComplete` weren't changing.
We can fix this using another change listener.
This one introduces special syntax.
By `**` we listen for any changes in the sub tree. 
This includes add/removed todos as well as any changes of their properties.

    :::java
    @ChangeListener(pattern = {
            "root.model.tasks",
            "root.model.tasks.**"})
    public void updateItemsValues() {
        modelRef.appendPath("itemsLeft").setValue(taskRepository.getActiveTasks().size());
        modelRef.appendPath("itemsComplete").setValue(taskRepository.getCompletedTasks().size());
    }
    


[1]: #linkToDocu

