### The TodoApp component

In this step we will learn how to access values form a `Ref`.
We will then use them to implement the render method of our `TodoApp` component.

#### TodoApp render

If you take a look the TodoMVC user interface you will notice that it can be split into three separate parts:

* A *header* containing the input field for new tasks
* A *main* section that contains a list of *todoItems*
* A *footer* with to select a filter and some meta information

So we want the render function in `todoApp.jsx` look like this:
    
    :::js
    render: function () {
      var header, main, footer, todoItems;
      
      // TODO
      
      return (
        <div>
          {header}
          {main}
          {footer}
        </div>
        );
    }
    
#### Header

Let's implement the header first, as it is not dependent on any view model properties:

    :::js
    header =
      <header id="header">
        <h1>todos</h1>
        <input
        ref="newField"
        id="new-todo"
        placeholder="What needs to be done?"
        autoFocus={true}
        onKeyDown={this.handleNewTodoKeyDown}
        />
      </header>;
      
Setting attributes like `onKeyDown` is React's way of listening for events. 
Note that the `handleNewTodoKeyDown` method is currently not defined. 
Let's add it to our component:

    :::js
    return React.createClass({
      handleNewTodoKeyDown: function (event) {
        // TODO
      },
    
      render: function() {
        ...
        
We will leave it empty for now and implement it in the next step.
        
#### Getting the values

Before we go on we need to access the values of our view model.
As we already know we can access the model ref via `this.props`.
By invoking `getValue` on the ref we get the underlying property. 
In our case it's a simple object containing the state of our application as detailed in the previous step.
    
    :::js
    var model = this.props.modelRef.getValue();
    var tasks = model.tasks;
    var tasksRef = this.props.modelRef.appendPath("tasks");
    
If you look at the behaviour of the TodoMVC application you will notice that the *main* section and the *footer* are only visible when there are todos in the list.
There is already a property in your view model that captures this behaviour.
We can now access it via the `model` object:

    :::js
    if (model.footerVisibility === true) {
      // footer = ...
      // todoItems = ...
      // main = ...
    }
   
#### Main section
    
Next we will define the main section:
    
    :::js
    main =
      <section id="main">
        <input
        id="toggle-all"
        type="checkbox"
        checked={model.toggleAll}
        onChange={this.toggleAll}
        />
        <ul id="todo-list">
          {todoItems}
        </ul>
      </section>;
    
Again we need to define the callback method for the `onChange` event which we will leave empty for now:

    :::js
    toggleAll: function () {
      // TODO
    },
    
#### Footer

The footer and the todo items will be encapsulated in their own components. 
As with the the `TodoApp` component we need to create new files: `todoFooter.jsx` and `todoItem.jsx`.
The outline of them should look like this:

    :::js
    /**
     * @jsx React.DOM
     */
    define([
      "react"
    ], function (React) {
      return React.createClass({
        render: function () {
          return <div/>;
        }
      });
    });
    
Since we have defined minimal `TodoFooter` and `TodoItem` components, we can now "import" them in our `TodoApp`.
We do so by adding `"build/todoApp"` and `"build/todoItem"` to the array as well as `TodoFooter` and `TodoItem` to the callback parameters:

    :::js
    define([
      "react",
      "build/todoFooter"
      "build/todoItem"
    ], function (React, TodoFooter, TodoItem) {
   
Now that we have the `TodoFooter` component in scope, we can assign a new instance to the footer variable:

    :::js
    footer =
      <TodoFooter
      model={model}
      onClearCompleted={this.clearCompleted}
      />;
      
We set our model and a `onClearCompleted` callback as attributes.
Don't forget to define an empty `clearCompleted` method.

#### Todo Items

For the todo items we need to map over the array of tasks and a return a `TodoItem` component for each entry.

    :::js
    todoItems = tasks.map(function (todo, i) {
      return (
        <TodoItem
        key={todo.id}
        modelRef={tasksRef.appendIndex(i)}
        model={todo}
        onDestroy={this.destroy.bind(this, i)}
        />);
    }, this);
    
A `TodoItem` has four attributes:
    
* A unique `key` attribute should be present on components that are stored in an array.

* The `modelRef` attribute should be a `Ref` to the current task in the list. 
To get that ref we use a special method for references to collections.
The `appendIndex` method is a bit like `appendPath`.
It does the same as constructing a path like `model.tasks[i]`, where `model.tasks` is our `tasksRef` and `i` is the number that get passed to `appendIndex`.

* The `model` contains the state of a single todo item.

* The `onDestroy` callback is another method in `TodoApp` that we need to implement.
Note that we call `bind` on it. 
`bind` returns a new function.
The first parameter of `bind` sets what `this` will be inside the new function.
All following parameters will be prepended to its argument list.
This has the effect that each `TodoItem` gets its own `destroy` function, that will always be invoked with the correct index as its first parameter.
Alternatively we could set the current index as an attribute and pass it to `onDestroy` ourselves when we invoke it.

For completeness here are the outlines of the two callback functions. 
Note the `i` parameter of `destroy` that we just bound in the map callback.

    :::js
    destroy: function (i) {
      // TODO
    },

    clearCompleted: function () {
      // TODO
    },
    
In the next step we will implement the four empty callback methods that we have created in this step.