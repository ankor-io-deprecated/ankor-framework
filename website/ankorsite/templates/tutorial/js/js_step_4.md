### The TodoApp component

In this step we will learn how to access values form a `Ref`.
We will then use them to implement the render method of our `TodoApp` component.

#### TodoApp render

If you take a look the TodoMVC UI we can split it into three separate parts:

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

Let's define what the header is first, as it is not dependent on any view model properties:

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
      
Setting attributes like `onKeyDown` is React's way of listening to events. 
The `handleNewTodoKeyDown` method is currently undefined. 
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

Before we start we need to access the values of our view model.
As we know form the previous step we can access the `Ref` to our model via `this.props`.
Via `getValues` we can get the underlying property. 
In our case its a simple JavaScript object containing the state of your application as detailed in the previous step.
    
    :::js
    var model = this.props.modelRef.getValue();
    var tasks = model.tasks;
    var tasksRef = this.props.modelRef.appendPath("tasks");
    
If you look at the behaviour of the TodoMVC application you will notice that the *main* section and the *footer* are only visible if there are todos in the list.
There is already a property in your view model that captures this behaviour.
We can now access it via the `model` object.

    :::js
    if (model.footerVisibility === true) {
      // footer = ...
      // todoItems = ...
      // main = ...
    }
   
#### Main section
    
In the last part of this step we will define the main section:
    
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
    
Again we need to define the callback function for the `onChange` event which we will leave empty for now:

    :::js
    toggleAll: function () {
      // TODO
    },
    
#### Footer

The footer and the todo items will be encapsulated in their own components. 
As with the the `TodoApp` component we need to create new files: `todoFooter.jsx`, `todoItem.jsx`.
The outline looks like this:

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
    
After we have defined the `TodoFooter` and `TodoItem` components, we need to "import" them via requirejs.
Add `"build/todoApp"`, `"build/todoItem"` to the dependency array and add `TodoFooter` and `TodoItem` parameters to the callback function:

    :::js
    define([
      "react",
      "build/todoFooter"
      "build/todoItem"
    ], function (React, TodoFooter, TodoItem) {
   
Now that we have the `TodoFooter` component in scope, we can assign to the footer var:

    :::js
    footer =
      <TodoFooter
      model={model}
      onClearCompleted={this.clearCompleted}
      />;
      
We pass our model to the footer component and a `onClearCompleted` callback (which you need to define) to our `TodoApp` component.

#### Todo Items

For the todo items we need to map over the array and a return a `TodoItem` component for every entry.

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
    
* The `key` attribute should be unique and is required by React for components in an array.

* For the `modelRef` attribute we use a special method on the `Ref` for references on collections.
The `appendIndex` method is similar to `appendPath`, except that it is available only for collections. 
It is similar to constructing a path like `model.tasks[i]`, where `model.tasks` is our `tasksRef` and `i` is the number that get passed to `appendIndex`.

* The `model` is an entry in the array and represents a single todo item.

* The `onDestroy` callback is another method in `TodoApp` that we need to implement.
Note that we call `bind` on it. 
This binds the current index to the function, so that the context is set to `this` and the first call parameter is the index of the todo.
Alternatively we could set the current index as an attribute to the component and pass it to `onDestroy` ourselves when we invoke it.

For the sake of completeness, here are the outlines of the two callback functions. 
Note the `i` parameter on `destroy`:

    :::js
    destroy: function (i) {
      // TODO
    },

    clearCompleted: function () {
      // TODO
    },
    
In the next step we will implement the four callback methods that we have defined in this step.
    
    
