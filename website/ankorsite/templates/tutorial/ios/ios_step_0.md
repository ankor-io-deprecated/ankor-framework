### iOS Client

In this tutorial we'll be enhancing a simple iOS application. We'll explain how to connect to the Ankor Todo Sample
and bind your UI components to the todo model. While Ankor is generally independent of the transport layer,
in this tutorial we'll be using WebSockets.

Checkout [iOS Apps tutorial][1] for detailed information this app.

#### Before you start

Please make sure that all software components are installed properly.

<div class="tabbable ">
    <ul class="nav nav-tabs">
        <li><a href="#tab1" data-toggle="tab">Git</a></li>
        <li><a href="#tab2" data-toggle="tab">XCode</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="tab1">
            <p>Install Git, download from <a href="http://git-scm.com/download">the Git site</a>.</p>
        </div>
        <div class="tab-pane active" id="tab2">
            <p>Install Xcode 5.1 via osx App Store.</p>
        </div>
    </div>
</div>


#### The Application

This his how the app will look like when we are done:

![ios-step-0-1](/static/images/tutorial/ios-step-0-1.png)

#### Get the code

Clone the git repository from:

    :::bash
    git clone https://github.com/ankor-io/ankor-todo-tutorial.git

The folder ankor-todo is empty. To get the first tutorial step, checkout branch `ios-step-0`.
This is how you may switch between tutorial steps later.

    :::bash
    cd ankor-todo
    git checkout -f ios-step-0

Now you got a maven project based on these modules:

    ankor-ios      : Ankor iOS Framework Xcode Project
    todo-ios       : iOS Todo Sample Xcode Project

#### Open Xcode Project

Open the TodoList Xcode Project in `todo-ios` folder. Now you see the todo list project which includes the
AnkorIOS Framework project.

![ios-step-0-1](/static/images/tutorial/ios-step-0-2.png)

[1]: https://developer.apple.com/library/iOS/referencelibrary/GettingStarted/RoadMapiOS/index.html
