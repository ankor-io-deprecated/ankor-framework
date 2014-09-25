### Testing the Setup

In order to see the results of our work we need to connect a client to our Ankor server.
The clients will connect to the server via [WebSocket][2]. 

Before we can start the server we need to fetch all the dependencies. 
In the root directory execute the maven goal `package`:

    mvn package

To start the web server you have several possibilities. 
You can either execute the maven goal inside the `todo-servlet` directory:

    cd todo-servlet
    mvn spring-boot:run
    
or start the generated the `jar` file directly

    cd todo-servlet/target
    java -jar todo-servlet-0-SNAPSHOT.jar
    
or you tell your IDE to run the main method in `ServerStarter.java`, 
which might be the best option for debugging.
    
After you have started the server, point your browser to [`http://localhost:8080/test.html`](http://localhost:8080/test.html).
The page should look something like this:

    :::text
    Server is up and running!
    Connecting to ws://localhost:8080/websocket/ankor/6684908722527325 ...
    WebSocket connection established

#### Connecting a client

If you went through one of the client tutorials you can use your own implementation.
Just make sure they connect to `ws://localhost:8080/websockets/ankor`.

Otherwise you can use the JavaScript client provided within this repository.
Simply point your browser to [`http://localhost:8080/`](http://localhost:8080/).
Of course the app will not load (a connection should be established though).
We will fix this in the next steps.

[1]: https://glassfish.java.net/
[2]: http://www.websocket.org/
