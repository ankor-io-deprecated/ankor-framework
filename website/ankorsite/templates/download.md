The best way to integrate Ankor in your project is via Maven:

    <repositories>
        <repository>
            <id>ankor-release</id>
            <url>http://repo.irian.at/content/repositories/ankor-release/</url>
            <releases><enabled>true</enabled></releases>
            <snapshots><enabled>false</enabled></snapshots>
        </repository>
    </repositories>
    
    
### Server

#### Service

For a server implementation you will need the `ankor-service` module:

    <dependency>
        <groupId>at.irian.ankor</groupId>
        <artifactId>ankor-service</artifactId>
        <version>$VERSION</version>
    </dependency>
    
For a concrete example see the [`pom.xml`][1] file of the Ankor todo sample server.

#### Servlet
 
To deploy an application in a servlet container add the `ankor-servlet` module:
    
    <dependency>
        <groupId>at.irian.ankor</groupId>
        <artifactId>ankor-servlet</artifactId>
        <version>$VERSION</version>
    </dependency>
    
For a concrete example see the [`pom.xml`][2] file of the Ankor todo sample servlet.
    
### Client

#### JavaFX

To integrate Ankor in a JavaFX application add the `ankor-fx` module.

    <dependency>
        <groupId>at.irian.ankor</groupId>
        <artifactId>ankor-fx</artifactId>
        <version>$VERSION</version>
    </dependency>
    
For a concrete example see the [`pom.xml`][3] file of the Ankor todo sample JavaFX client.
<br/>
<br/>
<br/>

[1]: https://github.com/ankor-io/ankor-todo-tutorial/blob/master/todo-application/pom.xml
[2]: https://github.com/ankor-io/ankor-todo-tutorial/blob/master/todo-servlet/pom.xml
[3]: https://github.com/ankor-io/ankor-todo-tutorial/blob/master/todo-fx/pom.xml
