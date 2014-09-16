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
        transport: new WebSocketTransport("/websocket/ankor", {
            connectProperty: "root"
        }),
        utils: new jQueryUtils($)
    });
    window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

    //Setup main UI
    var rootRef = ankorSystem.getRef("root");
    var contentRef = rootRef.append("contentPane");
    var panelsRef = contentRef.append("panels");
    rootRef.addPropChangeListener(function() {
        //Connect new tab buttons
        $("#newSearchTab").click(function() {
            contentRef.fire("createAnimalSearchPanel");
        });
        $("#newDetailTab").click(function() {
            contentRef.fire("createAnimalDetailPanel");
        });

        //Bind userName and serverStatus
        $("#userName").ankorBindInnerHTML(rootRef.append("userName"));
        $("#serverStatus").ankorBindInnerHTML(rootRef.append("serverStatus"));

        //Bind locale
        var selectLanguage = $("select.language");
        selectLanguage.ankorBindSelectItems(rootRef.append("supportedLocales"), { emptyOption: false });
        selectLanguage.ankorBindInputValue(rootRef.append("locale"));

        //Initialize tabs
        $("#tabs").tabs();
        var syncPanels = function() {
            console.log("Syncing panels");

            //Check for existing panels that need to be rendered
            var panels = panelsRef.getValue();
            for (var panelId in panels) {
                if ($("#tabs > ul > li." + panelId).length == 0) {
                    var panelRef = panelsRef.append(panelId);
                    var panel = panelRef.getValue();
                    if (panel.type == "animalDetail") {
                        new AnimalDetailTab(panelRef);
                    }
                    else if (panel.type == "animalSearch") {
                        new AnimalSearchTab(panelRef);
                    }
                }
            }

            //Check for rendered panels that need to be removed
            $("#tabs > ul > li").each(function(index, element) {
                var tabHeader = $(element);
                var tabId = tabHeader.attr("data-tabid");
                var tabBody = $("#tabs #tab-" + tabId);

                if (!(tabId in panels)) {
                    tabHeader.remove();
                    tabBody.remove();
                    $("#tabs").tabs("refresh");
                }
            });

            //Update i18n (After every panelSync currently is perfect for updating new/changed UI
            $.ankorStringMap("data-ankor-i18n", ankorSystem.getRef("root.resources"));
        };
        syncPanels();
        panelsRef.addTreeChangeListener(function(panelRef, event) {
            if (event.path.equals(panelsRef.path) || event.path.parent().equals(panelsRef.path)) {
                syncPanels();
            }
        });
    });

    //Send Ankor Init Message
    rootRef.fire("init");
});
