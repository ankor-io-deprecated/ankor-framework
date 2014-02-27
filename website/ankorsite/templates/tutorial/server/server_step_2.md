### The Model Root

An Ankor server is basically a composition of POJOs.
These objects have properties that reflect the current state of the UI.
In that respect they are "view models".
So it's common for them to extend the domain objects by some meta data that is only associated with their representation.

For example, in this tutorial we have domain-level [`Task`][1] objects.
They have a `title` and are either `completed` or not.
The `TaskModel`, which is the view model of a `Task`, will have an additional `editing` field.
This field indicates whether the todo is currently being edited in the UI.
Obviously this property would only pollute the domain object.
It shouldn't be persisted in a database either.
But it fits quite naturally into the view model.

View models don't need to extend domain object necessarily.
They also contain the "top-level" state of the application, like the title, labels and so on.

#### The Structure of an Ankor Server

At the very top of the hierarchy sits the `ModelRoot` object.
It contains all other view models .
In our case it will have only a single property, called `model`.
The `model` will be another view model that contains the state of the task list.

Here is how `ModelRoot` should look like:

    :::java
    public class ModelRoot {
        private TaskListModel model;

        public ModelRoot(Ref rootRef, TaskRepository taskRepository) {
            AnkorPatterns.initViewModel(this, rootRef);
            this.model = new TaskListModel(rootRef.appendPath("model"), taskRepository);
        }
    }

#### Constructor parameters

The constructor takes a Ankor [`Ref`][2] and a [`TaskRepository`][3] as parameters.

##### References

A core concept of Ankor is the `Ref`.
It represents a reference to a view model or one of its properties.
The Ref object allows us to navigate the view model hierarchy and manipulate the underlying properties.
The `rootRef` references the root of the hierarchy, which is an instance of the class that we are currently working on.

##### TaskRepository

The `TaskRepository` is already implemented for this tutorial.
Think of it as the service layer of the application.

#### Constructor body

A POJO is turned into a Ankor view model by invoking `AnkorPatterns.initViewModel` in the constructor.

The `TaskListModel` is the view model of the todo app. It takes a `Ref` to itself as a parameter.
We get this reference form the `rootRef` by "appending" `model` to the path.

So a path of `"root"` references `ModelRoot`, while `"root.model"` will reference the `TaskListModel`.

#### Providing access to properties

It's important that you add getters and setters for all properties that should be exposed to the client.
Let your IDE do that for you.

    :::java
    public TaskListModel getModel() {
        return model;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }


[1]: https://github.com/ankor-io/ankor-todo-tutorial/blob/server-step-2/todo-server/src/main/java/io/ankor/tutorial/model/Task.java
[2]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/ref/Ref.html
[3]: https://github.com/ankor-io/ankor-todo-tutorial/blob/server-step-2/todo-server/src/main/java/io/ankor/tutorial/model/TaskRepository.java
