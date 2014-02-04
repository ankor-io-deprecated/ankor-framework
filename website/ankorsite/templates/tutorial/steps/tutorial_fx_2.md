### Initializing Ankor

Open [`TaskListController.java`][1]. This is a controller in the JavaFX sense. A controller is attached to an
`.fxml` file by specifying it as an attribute in the root node. You can take a look at it in [`tasks.fxml`][2].

#### Initialize

Inside `initialize` we have to take care of two things:

    :::java
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = refFactory().ref("root"); // 1
        rootRef.fire(new Action("init")); // 2
    }

##### References

The core of the Ankor model is the [`Ref`](#TODOlinkToDoc). It is a reference to a property of the view model.
In this case it's a remote reference, as the view model resides on the server. All view model properties are ordered
in a hierarchical tree structure. The Ref object allows you to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `"root"` of the tree we get access to the complete view model. Except for
the root the tree is still empty though. That's why we need...

##### Actions

Another core concept of Ankor are Actions. An [`Action`](#TODOlinkToDoc) is generally used to make user interaction explicit. In this case
however we use it to tell the Ankor server to set up a new view model for us (you can think of it as
making the interaction that started the application explicit). An Action always gets invoked on a Ref, in this case it's
the root reference.  
The server will process the action and return a response containing the initial state of the application.
The data is JSON encoded and will look like this:

    ::javascript
    {
        "senderId": "ankor-servlet-server",
        "modelId": "fe03c887-e024-4e51-8af0-dc3d4d4de340",
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

`"model"` denotes the name of the property that has changed here. Its children are the names and values of the properties 
that have changed, like an (empty) array of tasks, the number of items in the list, and so on. 
It also has a type which is used to represent different kinds of changes like inserts or deletes.

When the message arrives, the client-side Ankor system will pick up the new values and update
its property tree. However there will be no visible change in the UI, since we haven't set up and bindings yet.

#### Adding annotation support

Before we get started we have to tell Ankor one more thing: To support annotations. It will allow us to write normal Java methods
in our `TaskListController` that will be executed when a certain property changes, which can come in handy sometimes. But
we want to avoid the boilerplate code of event listeners.

We add one line to our initialize method:

    ::java
    FXControllerAnnotationSupport.scan(rootRef, this);

Now we can create our own initialize method, the one that will be executed when the response from the server returns:

    ::java
    @ChangeListener(pattern = "root")
    public void myInit() {
        // TODO
    }
    
The `ChangeListener` annotation defines the kind of event that we are expecting. The `pattern` is the path to the
property that we want to listen for, in this case just `"root"`.   

This  method will be invoked whenever the root property changes, which is usually the case only once, 
when the server returns the view model after an init action has been received.

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-2/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-2/todo-javafx-client/src/main/resources/tasks.fxml