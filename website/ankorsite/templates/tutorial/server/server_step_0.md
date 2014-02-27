### Ankor Server

In this tutorial we'll be building an Ankor server that will receive messages from one of our
todo client apps, update its view model state and push those changes back to the client.

The server can run within any web container that implements [JSR 356][1], the Java API for WebSockets.
In this tutorial we'll be using an embedded [GlassFish 4][2] server, so you don't need to worry about it.

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
            <p>Maven 3.0.5 or higher, download from <a href="http://maven.apache.org/download.cgi">here</a>.</p>
            <p>Make sure that<p></p>
            <ul>
                <li>MAVEN_HOME exists in your user variables (JDK installation directory)</li>
                <li>and that %MAVEN_HOME%\bin is in your Path environment variable</li>
            </ul>
            <p>Open command line and test</p>
            <pre>mvn -version</pre>
        </div>
        <div class="tab-pane" id="tab3">
            <p>Install Git, download from <a href="http://git-scm.com/download">the Git site</a>.</p>
        </div>
    </div>
</div>

#### Get the code

Clone the git repository from:

    :::bash
    git clone https://github.com/ankor-io/ankor-todo-tutorial.git

The folder ankor-todo is empty. To get the first tutorial step, checkout branch `server-step-0`.
This is how you may switch between tutorial steps later.

    :::bash
    cd ankor-todo
    git checkout -f server-step-0

Now you got a maven project based on these modules:

    todo-fx      : Todo Sample - JavaFX Client
    todo-server  : Todo Sample - Server
    todo-servlet : Todo Sample - Servlet

The `todo-server` project contains the view model and its associated behaviour.
The `todo-servlet` project contains an endpoint that will expose the view model to clients via WebSocket.
The `todo-fx` contains a minimal JavaFX client to test your setup.

[1]: http://www.oracle.com/technetwork/articles/java/jsr356-1937161.html
[2]: https://glassfish.java.net/
