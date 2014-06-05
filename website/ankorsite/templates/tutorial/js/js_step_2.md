### Starting a AnkorSystem
 
The first thing we'd like to do in our `main.jsx` is to create an `AnkorSystem`. 
We've already defined the dependencies for the class in the previous step.

    :::js
    var ankorSystem = new AnkorSystem({
      modelId: "root",
      transport: new WebSocketTransport("/websocket/ankor", {
        "connectProperty": "root"
      }),
      utils: new BaseUtils()
    });
    
Most importantly the `AnkorSystem` request a transport object to handle sending and receiving messages.

Remember: Ankor is fully reactive. 
This means that events from the server are sent as soon as they are triggered.
This implies that there needs to be a push mechanism to the browser.
Currently only WebSockets are used by Ankor for this task. 

The `BaseUtils` are just a collection of utility functions.
You can provide your own implementation if you'd like.

The `modelId` and the `connectProperty` are both `root`, which is the name of the view model at the top of the hierarchy.
See the server tutorial for more information.

#### Getting the root ref

Next we need a `Ref` to our root model:
    
    :::js
    var rootRef = ankorSystem.getRef("root");
    
##### References

A core concept of Ankor is the [`Ref`][Ref]. It represents a reference to a view model.
Since we are writing a client application it's a remote reference to a view model on the server.
All view model properties are ordered in a hierarchical tree structure.
The Ref object allows us to navigate this tree and manipulate the underlying properties.
By requesting the reference that lies at the `root` of the tree we get access to the complete view model.

#### Rendering the app

Next we take full advantage of React's philosophy of re-rendering the entire app every time the state changes.
React makes this possible by constructing a "Shadow DOM" in JavaScript and only patching the diff into the actual DOM.
You can find out more at TODO.

So what we want is a `render` function that is called very time a change happens in any of the view models.
We achieve this by using the `addTreeChangeListener` method on the root ref and providing the yet-to-be-defined render function as a callback.

    :::js
    rootRef.addTreeChangeListener(render);
    
    function render() {
        // TODO
    }
    
In the next step we will implement the render function.
In order to do so we will have to learn a little bit more about React.

[Ref]: https://github.com/ankor-io/ankor-framework/blob/ankor-0.2/ankor-js/src/main/webapp/js/ankor/Ref.js
