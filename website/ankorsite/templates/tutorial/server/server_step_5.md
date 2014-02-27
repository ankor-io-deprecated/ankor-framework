### Adding Todos to the List

In the previous step we added todos to the repository.
This was equivalent to adding them to a database.

This alone will not make them appear in the UI though.
Note that the todo list in the UI can differ from the list in the database.
For example, this is the case when the user only wants to see the active or completed tasks.

#### Using a view model list

So what we need in our view model is a separate list of todos that represents the current list in the UI.
This list will change when the user selects a different filter.
We can use a simple `ArrayList` for this:

    :::java
    private List<TaskModel> tasks = new ArrayList<>();

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }


As you will notice we haven't defined a `TaskModel` yet.

#### Building view models form domain objects

The reason we need a `TaskModel` is that we want to keep the business layer and the UI separate.

A todo in the database has a `title` and a `completed` field.
However, a todo in the UI can also be edited.
This is why it needs an `editing` field as well.
But this field is only relevant in the UI and should not be part of the `Task` class.

So the `TaskModel` class extends the `Task` class and defines an additional `editing` property.

    :::java
    public class TaskModel extends Task {
        private boolean editing = false;

        public TaskModel(Task task) {
            super(task);
        }

        public boolean isEditing() {
            return editing;
        }

        public void setEditing(boolean editing) {
            this.editing = editing;
        }
    }

Since we will create `TaskModel`s on the fly they should have `equals` and `hashCode` methods.
You can let your IDE create them for you. You can also use these:

    :::java
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TaskModel taskModel = (TaskModel) o;

        return editing == taskModel.editing;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (editing ? 1 : 0);
        return result;
    }

#### Updating the list

With this in place we can add to our `newTask` method:

    :::java
    TaskModel model = new TaskModel(task);
    tasksRef().add(model);

As we've seen before, changing properties directly will not be noticed by Ankor.
Instead we've used `Ref`s.
For changing collections we can use a [`CollectionRef`][1] which is a subtype of `Ref`.
It has methods for manipulating the underlying collection.
Doing so will only send changed entries to the client.

`tasksRef()` is a helper method that returns the `CollectionRef` to our tasks property.
It is defined like this:

    :::java
    private CollectionRef tasksRef() {
        return modelRef.appendPath("tasks").toCollectionRef();
    }

#### Implementing the the delete action

Knowing about `CollectionRef`s we can also implement the `deleteTask` method:

    :::java
    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        Task task = tasks.get(index);
        taskRepository.deleteTask(task);

        int itemsLeft = taskRepository.fetchActiveTasks().size();
        modelRef.appendPath("itemsLeft").setValue(itemsLeft);

        tasksRef().delete(index);
    }

[1]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/ref/CollectionRef.html
