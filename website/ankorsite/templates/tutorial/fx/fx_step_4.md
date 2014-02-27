### Adding Todos to the List

In order to find out if our bindings from the previous step work we need to add todos to the list.
There's already an input field in the UI.
We just need to implement the `newTask` method in our [`TaskListController`][1].

As the [`@FXML`][3]  annotation of this method suggests it is defined in [`tasks.fxml`][2].
The `TextField` with the id `newTask` has an attribute `onAction="#newTodo"`.
This links the text field's action event to the method in our controller.

#### Get the TextField

First we need the reference to the `TextField`, so we add another field to our controller:

    :::java
    @FXML
    public TextField newTodo;

![fx-step-4-1](http://ankor.io/static/images/tutorial/fx-step-4-1.png)

Now we can get the text form the text field like this. Inside the `newTodo` method write:

    :::java
    String title = newTodo.getText();

#### Firing an Action with Parameters

We will use an `Action` to tell the server that the user wants to add a new todo.
On the server there's already a method defined for a action named `"newTask"`.
Unlike the `"init"` action this one takes parameters as well.
The parameters are stored in a map, which is passed to the constructor of an `Action`.

In addition you might want to prevent empty tasks from reaching the server:

    :::java
    if (!title.equals("")) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        modelRef.fire(new Action("newTask", params));
        newTodo.clear();
    }

<div class="alert alert-info">
  <strong>Note:</strong> If you followed the tutorial you won't have a reference to <code>modelRef</code> inside <code>newTask</code>. You can either create
  a private field in the controller and save the reference from the init method or call <code>appendPath</code> on the root ref as in the previous step.
</div>

After you insert a new todo and press Enter you should see the footer appear. This means that our bindings from step 3 work.
However, we still don't see our todos in the list yet.

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-4/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-4/todo-javafx-client/src/main/resources/tasks.fxml
[3]: http://docs.oracle.com/javafx/2/api/javafx/fxml/FXML.html
