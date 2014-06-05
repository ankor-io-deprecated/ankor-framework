### Components, Dependencies

In this step we are going to implement the top level React component TodoApp.
 
#### A minimal React component

First we need to create a new file called `todoApp.jsx` in the `src` folder.
This file will contain the component that describes the overall layout of the app:

    :::js
    /**
     * @jsx React.DOM
     */
    define([
      "react"
    ], function (React) {
        // TODO
    });
    
Next we will create a React component and return it as the requirejs module.
Replace the comment with:

    :::js
    return React.createClass({
      render: function() {
        return (
          <div>It works!</div>
        );
      }
    });
    
#### Adding a dependency to main.jsx
    
Now that we have a React component we can "require" it in `main.jsx`.
We do so by adding a `"build/todoApp"` to the list of dependencies in `define`. 
In addition we need a `TodoApp` parameter in the callback function:

    define([
      "ankor/AnkorSystem",
      "ankor/transport/WebSocketTransport",
      "ankor/utils/BaseUtils",
      "react",
      "build/todoApp"
    ], function (AnkorSystem, WebSocketTransport, BaseUtils, React, TodoApp) {
        
#### Implementing the render function

The render function will create an instance of our `TodoApp` component and patch it into the DOM.
React's `renderComponent` method will handle this for us.
It takes a React component as first parameter and a target DOM node as a second parameter.
As previously mentioned, any successive call to `renderComponent` will only patch the diff into that DOM node.

    function render() {
      React.renderComponent(
        <TodoApp modelRef={rootRef.appendPath("model")} />,
        document.getElementById('todoapp')
      );
    };

You may have noticed the inline XML. Again, `jsx` will translate it into valid JavaScript for us.

The other interesting thing is the `modelRef` attribute of the `TodoApp` tag. 
Attributes you pass to a React component that way will be available via `this.props` inside the component.

#### Navigating Refs

To understand the `appendPath` method that is available on Ankor `Ref`s we take a look at the overall structure of our application's view model:

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

Currently we have a `Ref` to the `root` property, called `rootRef`.
But we want access to the `model` and its various key-value pairs, which hold the actual state of the UI.
To navigate the tree we can "append" a path to a `Ref`, yielding a new `Ref` to the specified child node.

That's it for this step. In the next step we will implement the `TodoApp` component.
