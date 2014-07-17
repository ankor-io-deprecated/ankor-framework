### Source

You can get the source code from GitHub via:

    :::bash
    git clone https://github.com/ankor-io/ankor-framework.git

### Maven

The best way to integrate Ankor in your project is via Maven.
To find Ankor dependencies add the Ankor repository to your project:

    <repositories>
        <repository>
            <id>ankor-release</id>
            <url>http://repo.irian.at/content/repositories/ankor-release/</url>
            <releases><enabled>true</enabled></releases>
            <snapshots><enabled>false</enabled></snapshots>
        </repository>
    </repositories>
    
#### Bundles
    
There are six typical Ankor configurations or "bundles":

* `ankor-bundle-server-viewmodel`
* `ankor-bundle-socket-fx-client`
* `ankor-bundle-socket-server`
* `ankor-bundle-websocket-fx-client`
* `ankor-bundle-websocket-server`
* `ankor-bundle-js-client`

