define([
    "jquery",
    "ankor/AnkorSystem", // XXX: For testing
    "ankor/transport/WebSocketTransport",
    "ankor/utils/jQueryUtils",
    "./TaskList",
    "ankor/adapters/JQueryAdapter" //Require only, no reference needed
], function($, AnkorSystem, WebSocketTransport, jQueryUtils, TaskList) {
    $(function() {
        //Setup AnkorSystem
        var ankorSystem = new AnkorSystem({
            debug: true,
            senderId: null,
            modelId: "collabTest",
            transport: new WebSocketTransport(),
            utils: new jQueryUtils($)
        });

        window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

        var rootRef = ankorSystem.getRef("root");
        rootRef.addPropChangeListener(function() {
            new TaskList(rootRef.append("model"));
        });
        rootRef.fire("init");
    });
});

