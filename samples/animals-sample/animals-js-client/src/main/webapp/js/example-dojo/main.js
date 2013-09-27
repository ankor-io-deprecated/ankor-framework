define([
    "ankor/AnkorSystem",
    "ankor/transport/HttpPollingTransport",
    "ankor/utils/DojoUtils",
    "dojo/parser",
    "dijit/layout/LayoutContainer", //Includes only below here
    "dijit/layout/TabContainer",
    "dijit/layout/ContentPane",
    "dijit/MenuBar",
    "dijit/PopupMenuBarItem",
    "dijit/DropDownMenu",
    "dijit/MenuItem",
    "dijit/MenuBarItem"
], function(AnkorSystem, HttpPollingTransport, DojoUtils, parser) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        debug: true,
        modelId: "collabTest",
        transport: new HttpPollingTransport("/ankor"),
        utils: new DojoUtils()
    });
    window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

    //Init Dojo
    parser.parse();

    //Setup main UI
    var rootRef = ankorSystem.getRef("root");
    var tabsRef = rootRef.append("contentPane").append("panels");
    rootRef.addPropChangeListener(function() {
        /*//Connect new tab buttons
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
        });*/
    });

    //Send Ankor Init Message
    rootRef.fire("init");
});
