### Binding your first Property

In this step we are going to bind a JavaFX property to an Ankor property. As far as bindings are concerned, this will
be about the JavaFX API for the most part. Ankor tries to be as close to the JavaFX API as possible. 

#### Accessing JavaFX components

Before we can bind Ankor properties to UI components we need to access them in our controller.
In order to do so we need to specify the type of our component (`Node` being the most general one) and name 
it exactly like its id attribute in the [`tasks.fxml`][2]. Adding the `@FXML` annotation makes it explicit that there is
JavaFX magic happening in the background.

In this example we want to hide and show the footer based on the number of uncompleted tasks, as in the reference 
implementation. This means we need to access the footer (which is separated in two parts). Its ids are `footerTop` and `footerBottom`, 
so we'll need those two fields in our controller:

    :::java
    @FXML
    public Node footerTop;
    @FXML
    public Node footerBottom;
    
#### Navigating Ankor with Refs

Now that we have received the view model from the server we can bind UI properties. But right now we only got
a Ankor `Ref`, which obviously can't be bound to any JavaFX properties.

Now as you might have noticed earlier, our todo application's view model on the server is structured like this:

    :::javascript
    "root": {
        "model": {
            "tasks": [],
            "filter": "all",
            "itemsLeft": 0,
            ...
        }
    }

We only have a `Ref` to the `root` property right now, but we want access to the `model` and its various key-value pairs, which hold
the actual state of our application.
To navigate the tree we can "append" a path to a `Ref`, yielding a new `Ref` to the specified property.

So in order to access the `model` part of the view model we call inside our `myInit` method:

    :::java
    FxRef rootRef = refFactory().ref("root");
    FxRef modelRef = rootRef.appendPath("model");
    
Now that we got used to it, we do it once more:

    :::java
    FxRef footerVisibilityRef = modelRef.appendPath("footerVisibility");
    
<div class="alert alert-info">
  <strong>Note:</strong> We are using type FxRef instead of Ref here.
</div>

#### Binding Ankor properties to JavaFX properties
    
Now that we have a `Ref` to the footer-visibility-property and a Java references to the FX components we can finally 
do the binding.

Ankor provides a subtype of `Ref` called [`FxRef`][3], which has a method `fxProperty()`. 
As the name suggests it returns a property object. It can be used like any other JavaFX property, especially for bindings. 

    :::java
    Property<Boolean> footerVisibilityProperty = footerVisibilityRef.fxProperty();
    footerTop.visibleProperty().bind(footerVisibilityProperty);
    footerBottom.visibleProperty().bind(footerVisibilityProperty);
    
In the case of our `footerVisibilityRef` we are expecting a `Property` of type `Boolean`.
We then use this property and bind it to the visibilityProperty of the footer nodes.

We just learned how bindings work in JavaFX as well as in conjunction with Ankor. Unfortunately we still won't be able to see anything,
since the footer visibility won't change until we add a todo to the list.

[1]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/java/io/ankor/tutorial/TaskListController.java
[2]: https://github.com/ankor-io/ankor-todo/blob/fx-step-3/todo-javafx-client/src/main/resources/tasks.fxml
[3]: #TODOlinktoDocs
