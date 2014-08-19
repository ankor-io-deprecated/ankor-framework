##### JavaScript Client

A `js-client` module is basically a static webapp that connects to a server via web socket.
It depends on `ankor-bundle-js-client` for the RequireJS modules.

Exemplary `pom.xml`:

    <groupId>com.example</groupId>
    <artifactId>your-js-client</artifactId>
    <packaging>war</packaging>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>at.irian.ankor</groupId>
            <artifactId>ankor-bundle-js-client</artifactId>
            <version>0.3.0</version>
            <type>pom</type>
        </dependency>
    </dependencies>
    
For more details check out the [`hello-js-client`](https://github.com/ankor-io/hello-ankor/tree/master/hello-js-client) module.
