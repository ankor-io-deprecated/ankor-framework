### Ankor Server

In this tutorial we'll be building an Ankor server that will receive messages from one of our
todo client apps, update its view model state and push those changes back to the client.

The server can run within any web container that implements [JSR 356][jsr356] (the Java API for WebSockets).
However, in this tutorial we'll be using [Spring Boot][springboot], 
mostly because it is easy to set up and can be started form a `main` method (internally it uses Tomcat).

All the dependencies (including Spring Boot) are defined in the `pom.xml` file, so don't worry about it.
However, there are a couple of things you will need to install yourself.

#### Before you start

Please make sure that all software components are installed properly.

<div class="tabbable ">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab1" data-toggle="tab">Java</a></li>
        <li><a href="#tab2" data-toggle="tab">Maven</a></li>
        <li><a href="#tab3" data-toggle="tab">Git</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="tab1">
            <p>JDK 1.7, download from <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">here</a>.</p>
            <p>Make sure that<p></p>
            <ul>
                <li>JAVA_HOME exists in your user variables (JDK installation directory)</li>
                <li>and that %JAVA_HOME%\bin is in your Path environment variable</li>
            </ul>
            <p>Open command line and test</p>
            <pre>java -version</pre>
        </div>
        <div class="tab-pane" id="tab2">
            <p>Maven, download from <a href="http://maven.apache.org/download.cgi">here</a>.</p>
            <p>Open command line and test</p>
            <pre>mvn -version</pre>
        </div>
        <div class="tab-pane" id="tab3">
            <p>Git, download from <a href="http://git-scm.com/download">the Git site</a>.</p>
        </div>
    </div>
</div>

#### Get the code

Clone the git repository from:

    :::bash
    git clone https://github.com/ankor-io/ankor-todo-tutorial.git

To get the first tutorial step, checkout branch `server-step-0`.
This is also how you may switch between tutorial steps later:

    :::bash
    cd ankor-todo
    git checkout -f server-step-0

Now you got a maven project based on these modules:

    todo-fx        : Todo Sample - JavaFX Client
    todo-js-client : Todo Sample - JavaFX Client
    todo-server    : Todo Sample - Ankor Application Implementation
    todo-servlet   : Todo Sample - Spring Boot Server Starter

The `todo-server` project contains the view model and its associated behaviour.
The `todo-servlet` project will start a server and expose the view model to clients via WebSocket.
The `todo-js-client` project contains a browser client to test your setup.
The `todo-fx` project contains a JavaFX client to test your setup.

[jsr356]: http://www.oracle.com/technetwork/articles/java/jsr356-1937161.html
[springboot]: http://projects.spring.io/spring-boot/
