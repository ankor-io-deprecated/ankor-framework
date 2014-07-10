#### UI Binding support

Bind your View Model properties to UI controls. 
Ankor comes with binding support for several UI technologies. 
Here's a JavaFX example:

    :::java
    Ref rootRef = FxRefs.refFactory().ref("root");
    Ref nameRef = rootRef
        .appendPath("person.firstName");
    
    textCtrl.textProperty().bind(
        nameRef.fxProperty());
        
As a developer you just bind view model properties to your UI controls. 
The binding typically works both ways UI control to model and vice versa. 
