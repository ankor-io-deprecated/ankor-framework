### Adding Todos to the List

In order to find out whether our bindings work we need to add tasks to the list. The UI has an input field and is
already listening for the enter key being pressed. This is when the `newTask` method in our [`TaskListController`][1] is being called.  

As the `@FXML` annotation suggests this method is defined in [`tasks.fxml`][2]. On the `TextField` with the id `newTask`
you will find the attribute `onAction="#newTodo"`. This links the text fields action event to the method in our controller.

#### Get the TextField

Before we get started we need the reference to the `TextField` from the fxml, so we add another field to our controller:

    :::java
    @FXML
    public TextField newTodo;
    
Inside our `newTodo` function we can then get the text from the text field:

    :::java
    String title = newTodo.getText();
    
#### Firing an Action with Parameters

We will use an `Action` to tell the server that the user wants to add a new todo. On the server there is a handle defined
for a action named `"newTask"`. Unlike the `"init"` action this one takes parameters as well. 
The parameters are stored in a map, which is passed to the constructor of `Action`.Then we fire the action on the `modelRef`.  

    :::java
    if (!title.equals("")) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("title", title);
        modelRef.fire(new Action("newTask", params));
        newTodo.clear();
    }

In addition you might want to prevent empty tasks from reaching the server, by wrapping the call in an if.

<div class="alert alert-info">
  <strong>Note:</strong> If you followed the tutorial you won't have a reference to <code>modelRef</code> inside <code>newTask</code>. You can either create
  a private field in the controller and save the reference from the init method or append paths to the root ref as in the previous example.
</div>

After you insert a new todo and press Enter you should see the footer appear. This means that our bindings from step 3 work. 
However, we won't see our todos in the list yet and the number of items won't update, since we haven't defined the bindings it yet.

    
[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-4/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-4/todo-javafx-client/src/main/resources/tasks.fxml
