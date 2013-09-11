define([
    "jquery",
    "ankor/AnkorSystem", // XXX: For testing
    "ankor/transport/SocketIOTransport",
    "ankor/utils/jQueryUtils",
    "./TaskList",
    "ankor/adapters/JQueryAdapter" //Require only, no reference needed
], function($, AnkorSystem, SocketIOTransport, jQueryUtils, TaskList) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        debug: true,
        modelId: "collabTest",
        transport: new SocketIOTransport("/ankor"),
        utils: new jQueryUtils($)
    });

    window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

    var rootRef = ankorSystem.getRef("root");
    rootRef.addPropChangeListener(function() {
        new TaskList(rootRef.append("model"));
    });
    rootRef.fire("init");
});

