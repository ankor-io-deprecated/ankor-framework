define([
    "jquery",
    "ankor/AnkorSystem",
    "ankor/transport/HttpPollingTransport",
    "ankor/utils/jQueryUtils",
    "./TaskList",
    "ankor/adapters/JQueryAdapter" //Require only, no reference needed
], function($, AnkorSystem, HttpPollingTransport, jQueryUtils, TaskList) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        debug: true,
        modelId: "collabTest",
        transport: new HttpPollingTransport("/ankor"),
        utils: new jQueryUtils($)
    });

    window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

    var rootRef = ankorSystem.getRef("root");
    rootRef.addPropChangeListener(function() {
        new TaskList(rootRef.append("model"));
    });
    rootRef.fire("init");
});

