define([
    "jquery",
    "ankor/AnkorSystem",
    "ankor/transport/TestTransport",
    "./AnimalDetailTab",
    "./AnimalSearchTab",
    "jquery-ui", //Require only, no reference needed
    "ankor/adapters/JQueryAdapter" //Require only, no reference needed
], function($, AnkorSystem, TestTransport, AnimalDetailTab, AnimalSearchTab) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        modelId: null,
        transport: new TestTransport()
    });

    //Setup main UI
    var rootRef = ankorSystem.getRef("root");
    var tabsRef = rootRef.append("tabs");
    rootRef.addPropChangeListener(function() {
        //Connect new tab buttons
        $("#newSearchTab").click(function() {
            tabsRef.fire("createAnimalSearchTab");
        });
        $("#newDetailTab").click(function() {
            tabsRef.fire("createAnimalDetailTab");
        });

        //Bind userName and serverStatus
        $("#userName").ankorBindInnerHTML(rootRef.append("userName"))
        $("#serverStatus").ankorBindInnerHTML(rootRef.append("serverStatus"))

        //Initialize tabs
        $("#tabs").tabs();
        tabsRef.addTreeChangeListener(function(tabRef) {
            if (tabRef.parent().path() == "root.tabs") {
                var tab = tabRef.getValue();
                if (tab == null) {
                    $("#tabs ul li." + tabRef.propertyName()).remove();
                    $("#tabs #tab-" + tabRef.propertyName()).remove();
                    $("#tabs").tabs("refresh");
                }
                else {
                    //Create Tab
                    if (tab.type == "animalDetailTab") {
                        new AnimalDetailTab(tabRef);
                    }
                    else if (tab.type == "animalSearchTab") {
                        new AnimalSearchTab(tabRef);
                    }
                }
            }
        });
    });

    //Send Ankor Init Message
    rootRef.fire("init");

    console.log(ankorSystem);
});
