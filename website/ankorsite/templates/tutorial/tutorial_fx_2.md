### Initializing Ankor

Open [`TaskListController.java`](#linkToGithub). This is a controller in the JavaFX sense. A controller is attached to an
`.fxml` file by specifying it as an attribute in the root node. You can take a look at it in `tasks.fxml>`.

Inside `initialize` we have to take care of two things:

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = refFactory().ref("root");
        rootRef.fire(new Action("init"));
    }

#### References

At the core of the Ankor model is the [`Ref`](#linkToDoc). It is a reference to a property of the view model.
In this case it's a remote reference, as the view model resides on the server. All view model properties are ordered
in a hierarchical tree structure. The Ref object allows you to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `"root"` of the tree we get access to the complete view model. Except for
the root the tree is still empty though. That's why we need...

#### Actions

Another core concept of Ankor are Actions. An [`Action`](#linkToDoc) is generally used to make user interaction explicit. In this case
however we use it to tell the Ankor server to set up a new view model for us (you can think of it as
making the interaction that started the application explicit). An Action always gets invoked on a Ref, in this case it's
the root reference.  
The server will process the action and return a response containing the initial state of the application.
The data is JSON encoded and will look like this:

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

"model" denotes the name of the property that has changed here. Its children are the names and values of the properties 
that have changed, like an (empty) array of tasks, the number of items in the list, and so on. 
It also has a type which is used to represent different kinds of changes like inserts or deletes.

When the message arrives, the client-side Ankor system will pick up the new values and update
its property tree. However there will be no visible change in the UI, since we haven't set up and bindings yet.
