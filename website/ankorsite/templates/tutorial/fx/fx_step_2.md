### Initializing Ankor

Next, open [`TaskListController.java`][1]. This is a JavaFX controller. A controller is attached to an
`.fxml` file by specifying it as an attribute in the root node. You can take a look at it in [`tasks.fxml`][2].

#### Initialize

Inside `initialize` we have to take care of two things:

    :::java
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = App.refFactory().ref("root");
        rootRef.fire(new Action("init"));
    }

The first statement will return a `Ref`, the second one will send an `Action` to the server.

##### References

A core concept of Ankor is the [`Ref`][3]. It represents a reference to a view model.
Since we are writing a client application it's a remote reference to a view model on the server.
All view model properties are ordered in a hierarchical tree structure.
The Ref object allows us to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `root` of the tree we get access to the complete view model.
However, it is still empty except for the root. By firing an `Action` on root the view model will be initialized.

##### Actions

Another core concept of Ankor are Actions. An [`Action`][4] is generally used to make user interaction explicit.
In this case we use it to tell the Ankor server to set up a new view model for us.
The server will process the action and return a response containing the initial state of the application.
The data is JSON encoded and will look like this:

    :::javascript
    {
        "senderId": "ankor-servlet-server",
        "modelId": "...",
        "messageId": "ankor-servlet-server#1",
        "property": "root",
        "change": {
            "type": "value",
            "value": {
                "model": {
                    "tasks": [],
                    "filter": "all",
                    "itemsLeft": 0,
                    ...
                }
            }
        }
    }

`model` is the name of the node that has changed. Its children are the names and values of the properties
that have changed. These are an empty array of todos, the number of items that are left, and so on.
It also has a `type` which is used to represent different kinds of changes like inserts or deletes on collections.

When the message arrives, the client-side Ankor system will pick up the new values and update
its own view model. However there will be no visible changes in the UI, since we haven't set up any bindings yet.

#### Adding annotation support

Before we go on we have to tell Ankor to look for annotations. It will allow us to write normal Java methods
in our `TaskListController` that will be executed when a certain property changes. Otherwise we would have to write
a lot of boilerplate code for adding individual event listeners.

We add this line to our initialize method:

    :::java
    FXControllerSupport.init(this, rootRef);

We can now create our own initialize method, the one that will be called when the response from the server returns
after the `init` action has been fired:

    :::java
    @ChangeListener(pattern = "root")
    public void myInit() {
        // TODO
    }

The `ChangeListener` annotation says that we want to watch for changes on a property.
The `pattern` is the path to the property that we want to listen for, in this case it's the `root`.

In this method we will then implement bindings to UI components.

[1]: https://github.com/ankor-io/ankor-todo-tutorial/blob/fx-step-2/todo-fx/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo-tutorial/blob/fx-step-2/todo-fx/src/main/resources/tasks.fxml
[3]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/ref/Ref.html
[4]: http://ankor.io/static/javadoc/apidocs/at/irian/ankor/action/Action.html