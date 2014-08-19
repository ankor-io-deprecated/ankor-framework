##### Socket Server

A `socket-server` module depends on a your `server-viewmodel` module (see above) and the `socket-server` bundle.
It allows clients to connect via standard sockets.

Exemplary `pom.xml`:

    <groupId>com.example</groupId>
    <artifactId>your-socket-server</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>at.irian.ankor</groupId>
            <artifactId>ankor-bundle-socket-server</artifactId>
            <version>0.3.0</version>
            <type>pom</type>
        </dependency>

        <dependency>
            <groupId>com.example</groupId>
            <artifactId>your-application</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
    
For more details check out the [`hello-socket-server`](https://github.com/ankor-io/hello-ankor/tree/master/hello-socket-server) module.
