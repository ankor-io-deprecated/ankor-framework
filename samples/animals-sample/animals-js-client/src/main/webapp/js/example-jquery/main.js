define([
    "jquery",
    "ankor/AnkorSystem",
    "ankor/transport/HttpPollingTransport",
    "ankor/transport/WebSocketTransport",
    "ankor/utils/jQueryUtils",
    "./AnimalDetailTab",
    "./AnimalSearchTab",
    "jquery-ui", //Require only, no reference needed
    "ankor/adapters/JQueryAdapter" //Require only, no reference needed
], function($, AnkorSystem, HttpPollingTransport, WebSocketTransport, jQueryUtils, AnimalDetailTab, AnimalSearchTab) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        debug: true,
        modelId: "collabTest",
        // transport: new HttpPollingTransport("/ankor"),
        transport: new WebSocketTransport("/websocket/ankor"),
        utils: new jQueryUtils($)
    });
    window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

    //Setup main UI
    var rootRef = ankorSystem.getRef("root");
    var tabsRef = rootRef.append("contentPane").append("panels");
    rootRef.addPropChangeListener(function() {
        //Connect new tab buttons
        $("#newSearchTab").click(function() {
            tabsRef.fire("createAnimalSearchPanel");
        });
        $("#newDetailTab").click(function() {
            tabsRef.fire("createAnimalDetailPanel");
        });

        //Bind userName and serverStatus
        $("#userName").ankorBindInnerHTML(rootRef.append("userName"));
        $("#serverStatus").ankorBindInnerHTML(rootRef.append("serverStatus"));

        //Initialize tabs
        $("#tabs").tabs();
        tabsRef.addTreeChangeListener(function(tabRef, message) {
            //Remove tab
            if (tabRef.equals(tabsRef) && message.type == message.TYPES["DEL"]) {
                $("#tabs ul li." + message.key).remove();
                $("#tabs #tab-" + message.key).remove();
                $("#tabs").tabs("refresh");
            }
            //New tab (if not already exists)
            else if (tabRef.parent().equals(tabsRef) && $("#tabs ul li." + tabRef.propertyName()).length == 0) {
                var tab = tabRef.getValue();
                if (tab.type == "animalDetailTab") {
                    new AnimalDetailTab(tabRef);
                }
                else if (tab.type == "animalSearchTab") {
                    new AnimalSearchTab(tabRef);
                }
            }
        });
    });

    //Send Ankor Init Message
    rootRef.fire("init");
});
