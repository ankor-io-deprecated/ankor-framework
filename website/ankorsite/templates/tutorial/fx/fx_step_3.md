### Binding Properties

In this step we are going to bind a JavaFX property to an Ankor property. As far as bindings are concerned Ankor tries
to be as close to the JavaFX API as possible. If have used JavaFX before, this will look familiar to you.

#### Accessing JavaFX components

Before we can bind Ankor properties to UI components we need references to them in our controller.
In order to do so we need to specify the type of our component (`Node` being the most general one) and name
it exactly like its id attribute in [`tasks.fxml`][2].
Adding the [`@FXML`][5] annotation makes it explicit that this field is defined in the markup.

In this step we want to hide and show the footer (highlighted red in the image) based on the number of uncompleted tasks.
The footer should only be visible if there is at least one todo either completed or not.

The footer is split in two parts.
Its ids are `footerTop` and `footerBottom`, so we'll need those two fields in our controller:

    :::java
    @FXML
    public Node footerTop;
    @FXML
    public Node footerBottom;

![fx-step-3-1](http://ankor.io/static/images/tutorial/fx-step-3-1.png)

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
            "itemsLeftText": "items left",
            "footerVisibility": false,
            "itemsComplete": 0,
            "itemsCompleteText": "Clear completed (0)",
            "clearButtonVisibility": false,
            "toggleAll": true,
            "filterAllSelected": true,
            "filterActiveSelected": false,
            "filterCompletedSelected": false
        }
    }

We have a `Ref` to the `root` property right now, but we want access to the `model` and its various key-value pairs,
which hold the actual state of the UI.
To navigate the tree we can "append" a path to a `Ref`, yielding a new `Ref` to the specified child node.

    :::java
    FxRef rootRef = App.refFactory().ref("root");
    FxRef modelRef = rootRef.appendPath("model");
    FxRef footerVisibilityRef = modelRef.appendPath("footerVisibility");

<div class="alert alert-info">
  <strong>Note:</strong> We are using type <code>FxRef</code> instead of <code>Ref</code> here.
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

#### More bindings

Let's also bind the number of items in the list.
The number should be bound to the label `todoCountNum`
and the text ("items left" or "item left") should be bound to the label `todoCountText`.
Again, they are predefined in [`tasks.fxml`][2], so to reference them all we need is:

    :::java
    @FXML
    public Label todoCountNum;
    @FXML
    public Label todoCountText;

In our change listener method we can now bind the properties.
Binding the text is rather straight forward. However, note that we need to call `fxProperty` with a type parameter,
as it can't be inferred from the declaration in this context.

    :::java
    todoCountText.textProperty().bind(modelRef.appendPath("itemsLeftText").<String>fxProperty());

Binding the number is a bit trickier as this property is of type `Number`, while the label is expecting a `String`.
To work around this we can use a bidirectional binding, which allows us to specify a converter.
Alternatively we could have written our server to provide `itemsLeft` as a string.

    :::java
    todoCountNum.textProperty().bindBidirectional(
            modelRef.appendPath("itemsLeft").<Number>fxProperty(),
            new NumberStringConverter());

This is how bindings are done in JavaFX. Unfortunately we still won't be able to see anything,
since nothing will change until we add some todos to the list.

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/resources/tasks.fxml
[3]: #TODOlinktoDocs
[4]: http://docs.oracle.com/javafx/2/api/javafx/beans/property/Property.html
[5]: http://docs.oracle.com/javafx/2/api/javafx/fxml/FXML.html
