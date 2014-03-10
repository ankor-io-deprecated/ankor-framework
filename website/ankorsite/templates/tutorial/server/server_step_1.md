### Testing the Setup

In order to see the results of our work we need to connect a client to our Ankor server.
The clients will connect to the server via [WebSocket][2]. 
So we need a web server that supports them.
We will be using [GlassFish 4][1], because it comes with WebSocket support out of the box.

To start the web server `cd` into the root directory and execute this Maven goal:

    mvn install

This can take a while, since GlassFish will be downloaded.
After the process completes point your browser to [`http://localhost:8080/test.html`](http://localhost:8080/test.html).
The page should look something like this:

    :::text
    Up and Running!
    WebSocket connection established
    UUID received: 9b1ac725-5bdd-46d2-9b9e-e5a283ae057b

#### Connecting a client

If you went through one of the client tutorials you can use your own implementation.
Just make sure they connect to `ws://localhost:8080/websockets/ankor`.

Otherwise you can use the JavaScript client provided by this servlet.
Simply point your browser to `http://localhost:8080/`.
As of now you will only see a blank page and the servlet will throw an `NotImplementedException`.
Will will fix this in the next step.

[1]: https://glassfish.java.net/
[2]: http://www.websocket.org/
