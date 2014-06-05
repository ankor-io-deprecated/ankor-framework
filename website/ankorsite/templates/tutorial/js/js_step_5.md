### Firing action events

In this step we will implement the four methods that we left empty in the last step.
Here the beauty of React and Ankor will really shine, as all of them will be surprisingly short.
We will also get to know the Ankor concept of `Action`s.

#### Clear completed Todos

Let's start with `clearCompleted` as it is the simplest of all:
 
    :::js
    clearCompleted: function () {
      this.props.modelRef.fire("clearTasks");
    },
    
`clearTasks` is the name of the event handler as defined by the server.

Let's take a look at the `fire` method.
Effectively it fires a [`ActionEvent`][ActionEvent] that gets sent to the server by the Ankor system.
It will be processed there and `ChangeEvent`s will get sent back if the state changes.

As we've seen in a previous step there is a `TreeChangeListener` registered at the root model.
This means that for any change event that comes back, the overall render method will be called. 
React will find the differences to the current DOM and patch them into the DOM using a minimal set of API calls.
Since this process is fast we can run it for very change.

#### Removing a Todo

Next the `destroy` method:

    :::js
    destroy: function (i) {
      this.props.modelRef.fire("deleteTask", {index: i});
    },
    
It looks pretty mucht the same, except that the event handler on the server takes a parameter. 
Parameters get passed to `fire` as a "plain old" JavaScript object.
Note that the name `index` is determined by the server implementation.

#### Toggling all Todos 

All that the `toggleAll` method does is fire another action event. 
There difference this time is that we need to access the current state of the `toggleAll` property on the model and set it to its inverse.

    :::js
    toggleAll: function () {
      var modelRef = this.props.modelRef;
      var model = modelRef.getValue();
      modelRef.fire("toggleAll", {toggleAll: !model.toggleAll});
    },
    
#### Adding a new Todo

Adding a todo requires a bit more code.
    
First of all we need to import the `KEYS` object. 
It's just a enum-like object for the enter and escape key and already part of the repo.
The the call to `define` now looks like:

    :::js
    define([
      "react",
      "build/todoFooter",
      "build/todoItem",
      "build/keys"
    ], function (React, TodoFooter, TodoItem, KEYS) {
    
The implementation of `handleNewTodoKeyDown` looks like this:
    
    handleNewTodoKeyDown: function (event) {
      if (event.which === KEYS.ENTER_KEY) {
        var node = this.refs.newField.getDOMNode();
        var val = node.value.trim();
        if (val !== '') {
          this.props.modelRef.fire("newTask", {title: val});
          node.value = '';
        }
      }
    },
    
What may be confusing is the `this.refs.newField` part. 
This belongs to React and has nothing to do with the Ankor `Ref` concept. 
It is React's way of getting access to DOM elements. 
By setting a `ref` attribute on a component it will be present in the `refs` object.
In the case of the input field it looked like `<input ref="newField" ... />`.

The rest is hopefully self-explanatory or already known. 
Note that it is safe to use `e.which` because React implements it's own event system, where a `which` property is present.

This completes step 5. In the next step we will implement the `TodoItem` and `TodoFooter` components. 

[ActionEvent]: https://github.com/ankor-io/ankor-framework/blob/ankor-0.2/ankor-js/src/main/webapp/js/ankor/events/ActionEvent.js


