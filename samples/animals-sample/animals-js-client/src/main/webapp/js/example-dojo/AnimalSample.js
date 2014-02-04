define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin",
    "dijit/MenuItem",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "./AnimalDetailTab",
    "./AnimalSearchTab",
    "dojo/text!./templates/AnimalSample.html",
    "dijit/layout/LayoutContainer", //Includes only below here
    "dijit/layout/TabContainer",
    "dijit/layout/ContentPane",
    "dijit/MenuBar",
    "dijit/PopupMenuBarItem",
    "dijit/DropDownMenu"
], function(declare, lang, _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin, MenuItem, AnkorStatefulBinding, AnimalDetailTab, AnimalSearchTab, template) {
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {
        templateString: template,
        baseClass: "animalSampleMain",
        rootRef: null,
        i18nRef: null,

        //Attribute mappings - START

        labelUser: "",
        _setLabelUserAttr: { node: "spanUser", type: "innerHTML" },

        labelServerSays: "",
        _setLabelServerSaysAttr: { node: "spanServerSays", type: "innerHTML" },

        //i18n

        labelUserI18n: "",
        _setLabelUserI18nAttr: { node: "spanUserI18n", type: "innerHTML" },

        labelServerSaysI18n: "",
        _setLabelServerSaysI18nAttr: { node: "spanServerSaysI18n", type: "innerHTML" },

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
                    })),
                    this.rootRef.append("supportedLocales").addPropChangeListener(lang.hitch(this, "syncLanguages")),

                    //i18n
                    new AnkorStatefulBinding(this.popupAnimalMenu, "label", this.i18nRef.append("Animal")),
                    new AnkorStatefulBinding(this.popupAnimalMenu.popup.getChildren()[0], "label", this.i18nRef.append("SearchAnimals")),
                    new AnkorStatefulBinding(this.popupAnimalMenu.popup.getChildren()[1], "label", this.i18nRef.append("NewAnimal")),
                    new AnkorStatefulBinding(this.popupLanguageMenu, "label", this.i18nRef.append("Language")),
                    new AnkorStatefulBinding(this, "labelUserI18n", this.i18nRef.append("User_")),
                    new AnkorStatefulBinding(this, "labelServerSaysI18n", this.i18nRef.append("ServerSays_"))
                );

                this.syncTabs();
                this.syncLanguages();

                console.log(this.popupAnimalMenu.popup.getChildren()[0].get("label"));
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
                            i18nRef: this.i18nRef,
                            ankorPanelId: panelId
                        });
                    }
                    else if (panel.type == "animalSearch") {
                        newTab = new AnimalSearchTab({
                            panelRef: panelRef,
                            i18nRef: this.i18nRef,
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
        },

        syncLanguages: function() {
            var dropdown = this.popupLanguageMenu.popup;

            //Remove old menu items
            while (dropdown.hasChildren()) {
                dropdown.removeChild(0);
            }

            //Add new menu items
            var languages = this.rootRef.append("supportedLocales").getValue();
            for (var i = 0, language; (language = languages[i]); i++) {
                var menuItem = new MenuItem({
                    label: language,
                    onClick: lang.hitch(this, function(language) {
                        this.rootRef.append("locale").setValue(language);
                    }, language)
                });
                dropdown.addChild(menuItem);
            }
        }
    });
});
