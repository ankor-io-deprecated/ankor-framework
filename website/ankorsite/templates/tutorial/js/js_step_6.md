### TodoItem, TodoFooter

#### TodoItem

This is the complete render method of the `TodoItem` component in `todoItem.jsx`.
It should be straight-forward if you followed the previous steps.

    :::js
    render: function () {
      var model = this.props.model;
      
      var classes = React.addons.classSet({
        'completed': this.props.model.completed,
        'editing': this.props.model.editing
      });

      return (
        <li className={classes}>
          <div className="view">
            <input
            className = "toggle"
            type = "checkbox"
            checked = {model.completed}
            onChange = {this.onToggle}
            />
            <label onDoubleClick={this.setEditing.bind(this, true)}>
              {model.title}
            </label>
            <button
            className = "destroy"
            onClick = {this.props.onDestroy}
            />
          </div>
          <input
          ref = "editField"
          className = "edit"
          value = {model.title}
          onBlur = {this.setEditing.bind(this, false)}
          onChange = {this.handleChange}
          onKeyDown = {this.handleKeyDown}
          />
        </li>);
    }
    
The only new thing here is the React addon `classSet` which helps with setting style classes on DOM nodes.
The way it works is that the key is only present as a class if its value is true.

However, we are using a few callback methods that need to be implemented.
    
##### setEditing

As you can see in the render method, the call parameter gets bound to `true` on a double click and `false` when the input loses the focus.
Then it simply sets the value to the `editing` property of the todo's `Ref`. 
Calling `setValue` on a `Ref` will send a `ChangeEvent` to the server, where it may trigger other events.

    setEditing: function (value) {
      this.props.modelRef.appendPath("editing").setValue(value);
    },
    
##### handleKeyDown

This happens when the user types a key while editing the title of a todo.
In either case (escape or enter) the `editing` property of the todo should be set to `false`,
since the user finished editing the task.

    handleKeyDown: function (event) {
      var ref = this.props.modelRef.appendPath("editing");
      if (event.which === KEYS.ESCAPE_KEY || event.which === KEYS.ENTER_KEY) {
        ref.setValue(false);
      }
    },
    
<div class="alert alert-info">
    <strong>Note:</strong> Don't forget to import the KEYS object as shown in the previous step.
</div>

##### handleChange

This happens while the user is editing the title of a todo.
This sends the current title to the server by triggering a change event via `setValue`.

    :::js
    handleChange: function (event) {
      this.props.modelRef.appendPath("title").setValue(event.target.value);
    },
    
##### onToggle

Finally, this happens when to user completes a todo.
Again this sends the new value to the server by triggering a change event.

    :::js
    onToggle: function () {
      var ref = this.props.modelRef.appendPath("completed");
      ref.setValue(!this.props.model.completed);
    },
    
#### TodoFooter

The `TodoFooter` implementation should be straight-forward as well:

    :::js
    render: function () {
      
      var model = this.props.model;

      var clearButton;
      if (model.clearButtonVisibility) {
        clearButton =
          <button
          id="clear-completed"
          onClick={this.props.onClearCompleted}>
            {model.itemsCompleteText}
          </button>
      }

      var cx = React.addons.classSet;
      var filter = model.filter;
      var filterAll = cx({selected: filter === FILTER.ALL});
      var filterActive = cx({selected: filter === FILTER.ACTIVE});
      var filterCompleted = cx({selected: filter === FILTER.COMPLETED});
      
      return (
        <footer id="footer">
          <span id="todo-count">
            <strong>{model.itemsLeft}</strong> {model.itemsLeftText}
          </span>
          <ul id="filters">
            <li>
              <a href="#/" className={filterAll}>All</a>
            </li>
            {' '}
            <li>
              <a href="#/active" className={filterActive}>Active</a>
            </li>
            {' '}
            <li>
              <a href="#/completed" className={filterCompleted}>Completed</a>
            </li>
          </ul>
          {clearButton}
        </footer>);
    }
    
A few notes:

* `FILTER` is a object / enum and needs to be imported like `KEYS` in previous steps
* `{' '}` is to force white space between the list items
* `this.props.onClearCompleted` is a method from the parent that we passed to the footer in an earlier step

#### Router

You may have noticed that there are no callbacks on the filter. 
Hence the value of the `filter` property will never be changed.
This is because we are still missing a router, which will react to changes in the URL.
As you can see, clicking the "All", "Active" or "Completed" link will set the URL to `#/`, `#/active`, or `#/completed` respectively.

In our `TodoApp` component we add a method called `componentDidMount`. 
This method is part of React and will be called after the component enters the DOM.

Inside the method we create a `Router`.  
The router will listen for the URLs that we have defined in the footer and set the value of `filter` accordingly.

    :::js
    componentDidMount: function () {
      var filterRef = this.props.modelRef.appendPath("filter");
        
      var setFilter = function (value) {
        filterRef.setValue(value);
      };
      
      var router = Router({
        '/': setFilter.bind(this, FILTER.ALL),
        '/active': setFilter.bind(this, FILTER.ACTIVE),
        '/completed': setFilter.bind(this, FILTER.COMPLETED)
      });
      
      router.init('/');
    },
    
Note that we depend on `Router` and `FILTER` in order for this to work:
    
    :::js
    define([
      ...,
      "director",
      "build/filter"
    ], function (..., Router, FILTER) {
    
Congratulations! We have implemented the core functionality of our todo application.
This completes the HTML5 tutorial.
If you haven't done so yet, check out the corresponding [server tutorial][1].
Or you can learn how to build this application on [another platform][2].

[1]: http://www.ankor.io/tutorials/server
[2]: http://www.ankor.io/tutorials


