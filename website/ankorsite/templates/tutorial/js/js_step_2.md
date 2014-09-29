### Starting a AnkorSystem
 
The first thing we'd like to do in our `main.jsx` is to create an `AnkorSystem`. 
We've already defined the dependencies for the constructor in the previous step.
Inside the `define` callback we put:

    :::js
    var ankorSystem = new AnkorSystem({
      modelId: "root",
      transport: new WebSocketTransport("/websocket/ankor", {
        "connectProperty": "root"
      }),
      utils: new BaseUtils()
    });
    
The `AnkorSystem` needs a transport object to handle sending and receiving of messages.
In this case it is a `WebSocketTransport`, which is currently the only one available in the browser.

Remember that Ankor is fully reactive. 
This means that events from the server are sent to the client as they occur.
Consequently there needs to be a push mechanism to the browser.

The `BaseUtils` are just a collection of utility functions.
You can provide your own implementation if you'd like.

The value of `modelId` and `connectProperty` are both `"root"`, which is the name of the view model at the top of the hierarchy.
See the [server tutorial][servertutorial] for more information.

#### Getting the root ref

Next we need a `Ref` to our root model:
    
    :::js
    var rootRef = ankorSystem.getRef("root");
    
##### References

A core concept of Ankor is the [`Ref`][ref]. It represents a reference to a view model.
Since we are writing a client application it's a remote reference to a view model on the server.
All view model properties are ordered in a hierarchical tree structure.
The Ref object allows us to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `root` of the tree we get access to the complete view model.

#### Rendering the app

Now we take full advantage of React's philosophy of re-rendering the entire app.
We simply render the app every time the state changes.
React makes this possible by constructing a "virtual DOM" and only patching the difference into the actual DOM.

So what we need is a `render` function that gets called every time a change happens in any of the view models.
We achieve this by using the `addTreeChangeListener` method on the root ref.
We pass a yet-to-be-defined render function as a parameter:

    :::js
    rootRef.addTreeChangeListener(render);
    
    function render() {
        // TODO
    }
    
In the next step we will implement the render function.
In order to do so we will have to learn a little bit more about React though. 

[servertutorial]: http://ankor.io/tutorials/server
[ref]: https://github.com/ankor-io/ankor-framework/blob/ankor-0.4/ankor-js/src/main/webapp/js/ankor/Ref.js
