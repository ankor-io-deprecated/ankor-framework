### Reacting to Actions

In this step we will deal with Actions that are sent from the client.

#### Actions

Actions are another core concept of Ankor.
An [`Action`][1] is generally used to make user interaction explicit.

In this case we will deal with the `"newTask"` action.
This `Action` is triggered when the users creates a new todo.
It contains the title of the new task as a parameter.

#### Client: Firing Actions

You can skip this section if you went through any of the client tutorials.

For reference, the code for firing an Action on the JavaFX client looks like this:

    :::java
    Map<String, Object> params = new HashMap<>();
    params.put("title", title);
    modelRef.fire(new Action("newTask", params));

The API looks similar on other platforms.
Anyway, the server will receive JSON in this form:

    {
        "senderId": "...",
        "modelId": "...",
        "messageId": "...",
        "property": "root.model",
        "action": {
            "name": "newTask",
            "params": {
                "title": "test"
            }
        }
    }

As you can see, an `Action` always has a name.
Optionally it can have parameters.
If so, each of the parameters must have a name as well.

#### Server: Reacting to Actions

Instead of adding event listeners ourselves we will use Ankor's support for annotations.
We can turn a method into an action listener by annotating it with the [`@ActionListener`][2] annotation.
However there are a few things to consider:

1. The name of the method must be the same as the name of the Action.
2. If the Action has parameters, they will become call parameters and need to be annotated as well.

Let's see how this looks for the `newTask` Action:

    :::java
    @ActionListener
    public void newTask(@Param("title") final String title) {
        // ...
    }

Note the `@Param` annotation on the `title` parameter.

#### Implementing the newTask method

In the body of the method we create a new task and add it to the task repository.

    :::java
    Task task = new Task(title);
    taskRepository.saveTask(task);

Now we want to update the `itemsLeft` property to reflect the actual number of tasks in the repository.
Simply setting the property will not trigger any changes in the UI though.

    :::java
    int itemsLeft = taskRepository.fetchActiveTasks().size();

    // Ankor will not notice this
    this.itemsLeft = itemsLeft;

##### Using Refs to set properties

Instead we set the new value via a `Ref` that points at the `itemsLeft` property.
We can obtain this Ref by appending `"itemsLeft"` to our `modelRef`.

It is called "appending" because a `Ref` can also be thought of as a path.
This path leads from the `ModelRoot` to a property.
The full path to `itemsLeft` would be `"root.model.itemsLeft"`.  
Since we already have a `Ref` for to `"root.model"` we can simply "append" `"itemsLeft"`.

    :::java
    int itemsLeft = taskRepository.fetchActiveTasks().size();
    modelRef.appendPath("itemsLeft").setValue(itemsLeft);

This will send a change event to the client and trigger any events there.
It will also update the local variable, so that `(this.itemsLeft == itemsLeft)` evaluates to `true`.

After you restart the server, you can add todos to the list and watch how the number gets updated.

[1]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/action/Action.html
[2]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/annotation/ActionListener.html
