##### WebSocket JavaFX Client

A `websocket-fx-client` module contains a JavaFX application that connects to a server via web socket.

Exemplary `pom.xml`:

    <groupId>com.example</groupId>
    <artifactId>your-websocket-fx-client</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>
    
    <dependencies>
        <dependency>
            <groupId>at.irian.ankor</groupId>
            <artifactId>ankor-bundle-websocket-fx-client</artifactId>
            <version>0.4.0</version>
            <type>pom</type>
        </dependency>
    </dependencies>
    
For more details check out the [`hello-websocket-fx-client`](https://github.com/ankor-io/hello-ankor/tree/master/hello-websocket-fx-client) module.
