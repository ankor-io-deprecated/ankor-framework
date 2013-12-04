### Initializing Ankor

Open `TaskListController.java`. This is a controller in the JavaFX sense. A controller is attached to an
`.fxml` file by specifying it as an attribute in the root node. You can take a look at it in `tasks.fxml>`.

Inside `initialize` we have to take care of two things:

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = refFactory().ref("root");
        rootRef.fire(new Action("init"));
    }

#### Ankor references

At the core of the Ankor model is the `Ref`. It is a reference to a property of the view model.
In this case it's a remote reference, as the view model resides on the server. All view model properties are ordered
in a hierarchical tree structure. The Ref object allows you to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `"root"` of the tree we get access to the complete view model.

#### Actions

Another core concept of Ankor are Actions. An `Action` ...
