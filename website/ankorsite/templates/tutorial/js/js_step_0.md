### HTML5 Client

In this tutorial we'll be building a simple Ankor application for the browser.
We'll explain how to use the Ankor API in conjunction with the popular rendering framework [React][1].
While we use React in this example, the tutorial is intended to give you an overview on the Ankor JavaScript API, so that you can use it with other frameworks as well.

#### The Application

This his how the app will look like when we are done:

![js-step-0-1](http://ankor.io/static/images/tutorial/js-step-0-1.png)

This might look familiar to you. It's the todo app form [TodoMVC](http://todomvc.com/).

#### Before you start

Please make sure that all software components are installed properly.

<div class="tabbable ">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab1" data-toggle="tab">Java</a></li>
        <li><a href="#tab2" data-toggle="tab">Maven</a></li>
        <li><a href="#tab3" data-toggle="tab">Git</a></li>
        <li><a href="#tab4" data-toggle="tab">Node</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="tab1">
            <p>JDK 1.7.0 update 9 or higher, download from <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">here</a>.</p>
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
        <div class="tab-pane" id="tab4">
            <p>In addition to the usual requirements for Ankor tutorials we will need node.js and its package manager npm.</p>
            <p>Please refer to <a href="http://nodejs.org/">the node web page</a> on how to install node on your system. 
            Note that node.js comes bundled with npm.</p>
            <p>When you have node installed run <code>npm install -g react-tools</code>. 
            This will install a transformer for React components on your system.</p>
        </div>
    </div>
</div>

#### Get the code

Clone the git repository from:

    :::bash
    git clone https://github.com/ankor-io/ankor-todo-tutorial.git

To get the first tutorial step, checkout branch `js-step-0`.
This is how you may switch between tutorial steps later.

    :::bash
    cd ankor-todo
    git checkout -f js-step-0

Now you got a maven project based on these modules:

    todo-fx      : Todo Sample - JavaFX Client
    todo-server  : Todo Sample - Server
    todo-servlet : Todo Sample - Servlet

[1]: http://facebook.github.io/react/
