### Reloading the Todo List

In this step we want to update the todo list when the users selects one of the filters in the footer.

We add a [`Filter`][1] as a property in `TaskListModel` and initialize it with `Filter.all`.
We also add a boolean property for the active state of each of the filter buttons individually.

    :::java
    private Filter filter = Filter.all;
    private Boolean filterAllSelected = true;
    private Boolean filterActiveSelected = false;
    private Boolean filterCompletedSelected = false;

<div class="alert alert-info">
    <strong>Note:</strong>
    Don't forget to create getters and setters for these properties.
</div>


Next we add another change listener for the `filter` property.
This method will update the active state of the buttons at the bottom.
It will also reload the tasks based on the current filter.
We still have to implement the reload method.

    :::java
    @ChangeListener(pattern = "root.model.filter")
    public void updateFilterSelected() {
        modelRef.appendPath("filterAllSelected").setValue(filter.equals(Filter.all));
        modelRef.appendPath("filterActiveSelected").setValue(filter.equals(Filter.active));
        modelRef.appendPath("filterCompletedSelected").setValue(filter.equals(Filter.completed));
        reloadTasks(filter);
    }

`reloadTasks` fetches the list of tasks that should be visible to the user based on the selected filter.
It than transforms the `Task`s into `TaskModel`s and sets them as our `tasksRef`.

    :::java
    private void reloadTasks(Filter filter) {
        List<Task> tasks = taskRepository.fetchTasks(filter);
        List<TaskModel> taskModels = mapTasksToTaskModels(tasks);
        tasksRef().setValue(taskModels);
    }

`mapTasksToTaskModels` will loop through the task list and wrap each `Task` in a `TaskModel`:

    :::java
    private List<TaskModel> mapTasksToTaskModels(List<Task> tasks) {
        List<TaskModel> res = new ArrayList<>(tasks.size());
        for (Task t : tasks) {
            res.add(new TaskModel(t));
        }
        return res;
    }

Now we can click the filter in the UI and the list will reload.

#### Implementing the remaining actions

With the `reloadTasks` method we can also implement the remaining two action listeners.

##### Clear tasks

The functionality for this is already implemented in the repository.
We just have to update the UI state to reflect the changes.
We do so by calling our helper methods:

    :::java
    @ActionListener
    public void clearTasks() {
        taskRepository.clearTasks();
        updateItemsCount();
        reloadTasks(filter);
    }

##### Toggle all tasks

The other action changes the `completed` property of all todos to `true` if at least one todo isn't completed.
Otherwise it sets all tasks' completed property to `false`.

The method gets invoked with the target state as parameter.
Again, the functionality is already provided by the repository. 
We just update the item count and reload the task list.

    :::java
    @ActionListener
    public void toggleAll(@Param("toggleAll") final boolean toggleAll) {
        taskRepository.toggleAll(toggleAll);
        updateItemsCount();
        reloadTasks(filter);
    }

[1]: https://github.com/ankor-io/ankor-todo-tutorial/blob/server-step-7/todo-server/src/main/java/io/ankor/tutorial/model/Filter.java
