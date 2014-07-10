#### View Model

Implement your view model and all the behavior on server side. 
Let Ankor synchronize the data and propagate events across the wire.

    :::js
    "root": {
      "person": {
        "firstName": "John",
        "lastName": "Doe"
      }
    }
    
MVVM is an event-driven UI programming pattern originated from Microsoft. 
Using Ankor you are able to implement all the behavior on server side, 
whereas the state can be managed on the client and/or server side.