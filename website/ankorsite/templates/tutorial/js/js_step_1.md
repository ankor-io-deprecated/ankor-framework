### Basic Setup

There is not really a standard way for developing web apps. 
In order to achieve a common tasks such as modularization we have settled with the following libraries for the purpose of this tutorial.

* For modularization we use [`require.js`][1].
* As already noted we use [`React`][2] for rendering views.
* To install the above libraries we have used [`bower`][3]. 

#### Project structure

All the work will be done in the `todo-servlet` module. 
As you may know from the other tutorials, the servlet module is simply a WebSocket endpoint of the `todo-server` application.
Since a Glassfish instance is running anyway we might as well use it to serve the static webapp files.

#### Purpose of the React pre-compiler

The jsx pre-compiler makes writing React components a more convenient. 
It will compile components that look like this (note the inline `<div>` tag)

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
While React does not prevent you from writing the later directly, it is much more convenient to use the familiar HTML syntax.
    
#### Starting the React pre-compiler

Inside the `js` folder are two sub folders called `src` and `build`. 
We will only change `.jsx` files in the `src` folder. 
These files will then be automatically compiled and placed in the `build` folder.

To start the pre-compiler run `./jsx.sh` in the `js` folder. 
It will watch for file changes of `.jsx` files in `src`.
Make sure that you followed the instructions form the previous step so that you have the `react-tools` installed.

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
    
##### Purpose of the comment
The comment at the top indicated that this file needs to be passed through the React pre-compiler.
    
##### Structure of a require.js module
A require.js module starts with a call to `define` and lists a number of dependencies (without the `.js` at the end).
When all dependencies are loaded they are passed as arguments to a callback function. 
This allows the application developer to give names to each of them.

In `index.html` you can see this module being loaded:

    :::js
    require(["build/main"]);
    
As you can see it references the compiled file in the `build` folder, so again make sure the React pre-compiler works properly.

[1]: http://requirejs.org/
[2]: http://facebook.github.io/react/
[3]: http://bower.io/
