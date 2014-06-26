### Basic Setup

In order to achieve a common tasks like modularization we have settled with the following libraries for the purpose of this tutorial:

* For modularization we use [`requireJS`][requirejs].
* As already noted we use [`React`][react] for rendering views.
* To install the above libraries we have used [`bower`][bower] (not required).

#### Project structure

We will put our code in the `todo-servlet` module. 
As you might know from other tutorials, the servlet module is simply a WebSocket endpoint for the `todo-server` application.
Since  GlassFish instance is running anyway we use it to serve the static files for our web app as well.

#### Purpose of the React transformer

The JSX transformer makes writing React components more convenient. 
It will transform components with inline XML like this

    :::js
    /** @jsx React.DOM */
    var HelloMessage = React.createClass({
      render: function() {
        return <div>Hello {this.props.name}</div>;
      }
    });
    
into valid JavaScript like this:

    :::js
    /** @jsx React.DOM */
    var HelloMessage = React.createClass({displayName: 'HelloMessage',
      render: function() {
        return React.DOM.div(null, "Hello ", this.props.name);
      }
    });
    
The later will be used by React internally to run it's "diff magic" and to update the DOM.
Obviously React does not prevent you from writing your components in valid JS directly. 
However, it is recommended to use the JSX transformer.

For more information about JSX [see here][4].
    
#### Starting the React transformer

The `js` [folder][5] in `webapp` contains two sub folders called `src` and `build`.
We will only change `.jsx` files in the `src` folder. 
These files will be transformed and placed in the `build` folder by the JSX transformer.

To start the transformer run `./jsx.sh` in the `js` folder. 
It will watch for changes of our `.jsx` files.

If it fails, make sure that you followed the instructions form the previous step (Before you start > Node), so you have the React tools installed.

#### main.jsx

The prototypical `main.jsx` file should look like this:
    
    :::js
    /**
     * @jsx React.DOM
     */
    define([
      "ankor/AnkorSystem",
      "ankor/transport/WebSocketTransport",
      "ankor/utils/BaseUtils",
      "react"
    ], function (AnkorSystem, WebSocketTransport, BaseUtils, React) {
      // TODO
    });
    
##### Comment at the top
The comment at the top indicates that this file needs to be passed through the React transformer.
    
##### Structure of a requireJS module
A requireJS module starts with a call to `define` and lists a number of dependencies (without `.js` at the end).
After all dependencies have been loaded they are passed as arguments to the callback function. 
This basically achieves a name space in JavaScript.

In `index.html` you can see the above module being loaded:

    :::js
    require(["build/main"]);
    
As you can see it references the transformed file in the `build` folder, so again make sure you started the React transformer.

In the next step we will start an Ankor system that connects to a server.

[requirejs]: http://requirejs.org/
[react]: http://facebook.github.io/react/
[bower]: http://bower.io/
[4]: http://facebook.github.io/react/docs/jsx-in-depth.html
[5]: https://github.com/ankor-io/ankor-todo-tutorial/tree/js-step-1/todo-servlet/src/main/webapp/js
