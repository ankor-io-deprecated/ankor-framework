define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin",
    "ankor/adapters/DojoAdapter",
    "./AnimalDetailTab",
    "dojo/text!./templates/AnimalSample.html",
    "dijit/layout/LayoutContainer", //Includes only below here
    "dijit/layout/TabContainer",
    "dijit/layout/ContentPane",
    "dijit/MenuBar",
    "dijit/PopupMenuBarItem",
    "dijit/DropDownMenu",
    "dijit/MenuItem"
], function(declare, lang, _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin, ankorDojoAdapter, AnimalDetailTab, template) {
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {
        templateString: template,
        baseClass: "animalSampleMain",
        rootRef: null,

        //Attribute mappings - START
        labelUser: "",
        _setLabelUserAttr: { node: "spanUser", type: "innerHTML" },

        labelServerSays: "",
        _setLabelServerSaysAttr: { node: "spanServerSays", type: "innerHTML" },
        //Attribute mappings - END

        postCreate: function() {
            this.inherited(arguments);

            //Setup bindings
            this.rootRef.addPropChangeListener(lang.hitch(this, function() {
                this.own(
                    ankorDojoAdapter.bindStatefulAttribute(this, "labelUser", this.rootRef.append("userName")),
                    ankorDojoAdapter.bindStatefulAttribute(this, "labelServerSays", this.rootRef.append("serverStatus")),
                    this.rootRef.append("contentPane.panels").addTreeChangeListener(lang.hitch(this, function(ref, event) {
                        if (event.path.equals(ref.path) || event.path.parent().equals(ref.path)) {
                            this.syncTabs();
                        }
                    }))
                );

                this.syncTabs();
            }));

            //Send Ankor Init Message
            this.rootRef.fire("init");
        },

        onAnimalSearch: function() {
            this.rootRef.append("contentPane").fire("createAnimalSearchPanel");
        },

        onNewAnimal: function() {
            this.rootRef.append("contentPane").fire("createAnimalDetailPanel");
        },

        syncTabs: function() {
            console.log("sync tabs", this.tabContainer);

            var cp = new AnimalDetailTab();
            this.tabContainer.addChild(cp);

            /*
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
             */
        }
    });
});
