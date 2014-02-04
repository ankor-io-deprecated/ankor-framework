### Binding your first Property

In this step we are going to bind a JavaFX property to an Ankor property. As far as bindings are concerned Ankor tries
to be as close to the JavaFX API as possible. If have used JavaFX before, this will look familiar to you.

#### Accessing JavaFX components

Before we can bind Ankor properties to UI components we need references to them in our controller.
In order to do so we need to specify the type of our component (`Node` being the most general one) and name 
it exactly like its id attribute in [`tasks.fxml`][2]. Adding the `@FXML` annotation makes it explicit that there is
magic happening in the background.

In this step we want to hide and show the footer based on the number of uncompleted tasks, as in the reference
implementation of TodoMVC. This means we need to access the footer (which has actually two parts).
Its ids are `footerTop` and `footerBottom`, so we'll need those two fields in our controller:

    :::java
    @FXML
    public Node footerTop;
    @FXML
    public Node footerBottom;

#### Navigating Ankor with Refs

Now that we have received the view model from the server we can bind UI properties. But right now we only got
a Ankor `Ref`, which obviously can't be bound to any JavaFX properties.

As we've seen previously our todo application's view model is structured like this:

    :::javascript
    "root": {
        "model": {
            "tasks": [],
            "filter": "all",
            "itemsLeft": 0,
            ...
        }
    }

We have a `Ref` to the `root` property right now, but we want access to the `model` and its various key-value pairs,
which hold the actual state of the UI.
To navigate the tree we can "append" a path to a `Ref`, yielding a new `Ref` to the specified child node.

    :::java
    FxRef rootRef = refFactory().ref("root");
    FxRef modelRef = rootRef.appendPath("model");
    FxRef footerVisibilityRef = modelRef.appendPath("footerVisibility");

<div class="alert alert-info">
  <strong>Note:</strong> We are using type FxRef instead of Ref here.
</div>

#### Binding Ankor properties to JavaFX properties

Now that we have a `Ref` to the `footerVisibility` property and Java references to the UI components we can finally
do the binding.

Ankor provides a subtype of `Ref` called [`FxRef`][3], which has a method `fxProperty()`.
As the name suggests it returns a object of type [`Property`][4]. It can be used like any other JavaFX property,
including bindings:

    :::java
    Property<Boolean> footerVisibilityProperty = footerVisibilityRef.fxProperty();
    footerTop.visibleProperty().bind(footerVisibilityProperty);
    footerBottom.visibleProperty().bind(footerVisibilityProperty);

In the case of our `footerVisibilityRef` we are expecting a `Property` of type `Boolean`.
We then bind this property to the `visibleProperty` of the two footer nodes.

This is how one-way bindings are done in JavaFX. Unfortunately we still won't be able to see anything,
since the footer visibility won't change until we add a todo to the list.

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/resources/tasks.fxml
[3]: #TODOlinktoDocs
[4]: http://docs.oracle.com/javafx/2/api/javafx/beans/property/Property.html
