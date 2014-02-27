### Handling Events

By now we have the basic functionality of the app implemented.
What's left are either JavaFX specific event handlers or more Ankor `Action`s and Ankor bindings.

We will implement the missing features of the `TaskPane` first.
Then we will add cosmetic improvements like adding and removing style classes on some criteria.

Currently the app is missing these features:

* Double-click to edit a todo
* Deleting a todo

<div class="alert alert-info">
    <strong>Note:</strong>
    Completing todos is already possible, because we defined two-way bindings on the <code>completedProperty</code> in the previous step.
</div>

#### Double-click to edit a todo

In the previous step we left the `addEventListeners` method unimplemented.
Inside we add the functionality for changing the title of todos.
We've already defined a two-way binding in the previous step,
so when we edit the content of the `titleTextField` the server will automatically be informed about the change.

Currently the text field's editable property is set to `false`.
We want to set it to `true` when the user double-clicks a todo:

    :::java
    titleTextField.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getClickCount() > 1) {
                titleTextField.setEditable(true);
                titleTextField.selectAll();
            }
        }
    });

JavaFX is a bit odd here, but we know that we have a double-click when the click count is greater than 1.
In addition we select the entire text of the text field to be consistent with the reference implementation.

While it is not strictly required, we still want to reset the editable property to `false` when the user is done editing.
This is either the case when pressing Enter (1) or when the text field loses its focus (2).

    :::java
    // 1
    titleTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                titleTextField.setEditable(false);
            }
        }
    });

    // 2
    titleTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
            if (newValue == false) {
                titleTextField.setEditable(false);
            }
        }
    });

#### Deleting a todo

All we need to know for this one is that there is an `ActionListener` on the server called `deleteTask` that expects
the `index` of the todo as parameter. It's like `newTask` in step 4.

    :::java
    @FXML
    public void delete(ActionEvent actionEvent) {
        Map<String, Object> params = new HashMap<>();
        params.put("index", index);
        itemRef.root().appendPath("model").fire(new Action("deleteTask", params));
    }

#### Adding style classes

There is one thing that can't be done with JavaFX' CSS alone.
When a task is completed it's appearance should change.
There are already CSS classes for this, but we need to add/remove them when the task gets completed.

    :::java
    completedButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
            if (newValue == false) {
                titleTextField.getStyleClass().remove("default");
                titleTextField.getStyleClass().add("strike-through");
            } else {
                titleTextField.getStyleClass().remove("strike-through");
                titleTextField.getStyleClass().add("default");
            }
        }
    });

There are only a few things missing.
We need a few more bindings, but we already know how to do those.
Then we need to fire an `Action` when the user wants to toggle all todos.
We need another `Action` to clear the completed todos from the list.
We already know how to do those as well.