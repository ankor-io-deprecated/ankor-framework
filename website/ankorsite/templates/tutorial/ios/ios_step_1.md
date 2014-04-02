### Boot Ankor Client System

#### Run the Application

Now lets run the application. Select the iPhone Retina (4-inch 64-bit) Simulator and hit the run button.

![ios-step-0-1](/static/images/tutorial/ios-step-0-2.png)

Here is how the application looks like.

![ios-step-0-1](/static/images/tutorial/ios-step-0-1.png)

 As a next step we will connect to the Ankor Server.

#### Connect to the Ankor System

Open `AppDelegate.m` within `TodoList` folder in Xcode and uncomment the 2 lines as shown here:

    :::objectivec
    @implementation AppDelegate

    - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
    {
        // Uncomment next 2 lines to connect to the Ankor Server
        [[[ANKSystem alloc] initWith:@"root" connectParams:[[NSMutableDictionary alloc] init]
              url:@"ws://ankor-todo-sample.irian.at/websocket/ankor" useWebsocket:YES] start];

        return YES;
    }

We start the client side Ankor system and connect to Ankor Server via WebSockets.
An initial connect message will be sent which has 2 properties.

* `property`: Name of the Application (Model) we want to connect

* `connectParams`: A dictionary where we may pass application specific connection parameters

In the given code we will connect to model `root` without connectParams (empty NSMutableDictionary).

Now start the application again (just hit the run button) and take a look at the Xcode console.

    :::log
    2014-04-01 14:03:03.459 TodoList[3060:60b] webSocketDidOpen
    2014-04-01 14:03:03.460 TodoList[3060:60b] Sending json {"property":"root"}
    2014-04-01 14:03:03.479 TodoList[3060:60b] didReceiveMessage {"property":"root","change":{"type":"value","value":{"model":{"filter":"all","tasks":[],"footerVisibility":false,"itemsLeft":0,"itemsLeftText":"items left","clearButtonVisibility":false,"itemsComplete":0,"itemsCompleteText":"Clear completed (0)","filterAllSelected":true,"filterActiveSelected":false,"filterCompletedSelected":false,"toggleAll":false}}}}

`webSocketDidOpen` WebSocket connection established

`Sending json` Ankor client sent the connect message to the server

`didReceiveMessage` The server sent back the model data to the client

You are now connected. The next step explains how to bind the tasks list to the model.
