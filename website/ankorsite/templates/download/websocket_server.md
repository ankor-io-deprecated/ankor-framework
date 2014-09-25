##### WebSocket Server

A `websocket-server` module depends on a your `server-viewmodel` module (see above) and the `websocket-server` bundle.
It can run inside a servlet container that supports web sockets.

Exemplary `pom.xml`:

    <groupId>com.example</groupId>
    <artifactId>your-websocket-server</artifactId>
    <packaging>war</packaging>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>at.irian.ankor</groupId>
            <artifactId>ankor-bundle-websocket-server</artifactId>
            <version>0.4.0</version>
            <type>pom</type>
        </dependency>

        <dependency>
            <groupId>javax.websocket</groupId>
            <artifactId>javax.websocket-api</artifactId>
            <version>1.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.example</groupId>
            <artifactId>your-application</artifactId>
            <version>${project.version}</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
    
For more details check out the [`hello-websocket-server`](https://github.com/ankor-io/hello-ankor/tree/master/hello-websocket-server) module.
