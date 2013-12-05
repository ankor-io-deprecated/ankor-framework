define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "./AnimalDetailTab",
    "./AnimalSearchTab",
    "dojo/text!./templates/AnimalSample.html",
    "dijit/layout/LayoutContainer", //Includes only below here
    "dijit/layout/TabContainer",
    "dijit/layout/ContentPane",
    "dijit/MenuBar",
    "dijit/PopupMenuBarItem",
    "dijit/DropDownMenu",
    "dijit/MenuItem"
], function(declare, lang, _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin, AnkorStatefulBinding, AnimalDetailTab, AnimalSearchTab, template) {
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
                    new AnkorStatefulBinding(this, "labelUser", this.rootRef.append("userName")),
                    new AnkorStatefulBinding(this, "labelServerSays", this.rootRef.append("serverStatus")),
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
            var panelsRef = this.rootRef.append("contentPane.panels");
            var panels = panelsRef.getValue();

            //Create list of current tabs
            var tabs = {};
            var tabContainerChildren = this.tabContainer.getChildren();
            for (var i = 0, child; (child = tabContainerChildren[i]); i++) {
                tabs[child.ankorPanelId] = child;
            }

            //Check for new tabs to render
            for (var panelId in panels) {
                if (!panels.hasOwnProperty(panelId)) {
                    continue;
                }

                if (!(panelId in tabs)) {
                    var panelRef = panelsRef.append(panelId);
                    var panel = panelRef.getValue();
                    var newTab = null;

                    if (panel.type == "animalDetail") {
                        newTab = new AnimalDetailTab({
                            panelRef: panelRef,
                            ankorPanelId: panelId
                        });
                    }
                    else if (panel.type == "animalSearch") {
                        newTab = new AnimalSearchTab({
                            panelRef: panelRef,
                            ankorPanelId: panelId
                        });
                    }

                    if (newTab) {
                        tabs[panelId] = newTab;
                        this.tabContainer.addChild(newTab);
                        this.tabContainer.selectChild(newTab);
                    }
                }
            }

            //Check for tabs that have to be removed...
            for (var tabId in tabs) {
                if (!tabs.hasOwnProperty(tabId)) {
                    continue;
                }

                if (!(tabId in panels)) {
                    var tab = tabs[tabId];
                    this.tabContainer.removeChild(tab);
                    tab.destroyRecursive();
                }
            }
        }
    });
});
