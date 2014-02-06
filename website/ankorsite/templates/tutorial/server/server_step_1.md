### Starting the Server

#### Testing the setup

In order to see the results of our work in this tutorial we'll want to connect a client to our Ankor server.
The clients connect to the server via WebSocket, so we need a web server, [GlassFish 4][] in our case.

To start the web server `cd` into the `todo-servlet` directory and execute this Maven goal:

    mvn install

This can take a while. GlassFish will be downloaded.
After the process completes point your browser to `http://localhost:8080/`.
The page should look something like this:

    :::text
    Up and Running!
    WebSocket connection established
    UUID received: 9b1ac725-5bdd-46d2-9b9e-e5a283ae057b

In a new prompt `cd` into `todo-server` and run:

    mvn package

Now you can redeploy your app by pressing `Enter` in the command line that started the server.
You can shut down the server by typing `X`.

#### Connecting a client

If you went through one of the client tutorials you can use these.
Just make sure they connect to `ws://localhost:8080/websockets/ankor`.
Otherwise you can use the JavaFX client implementation provided in this repository:

    cd todo-fx
    mvn install
