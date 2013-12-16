define([
    "dojo/_base/declare",
    "dojo/_base/event",
    "dojo/store/Memory",
    "./BaseTab",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "dojo/text!./templates/AnimalDetailTab.html",
    "dijit/form/Form", //Includes only below here
    "dijit/form/TextBox",
    "dijit/form/FilteringSelect",
    "dijit/form/Button"
], function(declare, event, MemoryStore, BaseTab, AnkorStatefulBinding, template) {
    return declare([BaseTab], {
        templateString: template,

        //Attribute mappings - START
        labelNameStatus: "",
        _setLabelNameStatusAttr: { node: "spanNameStatus", type: "innerHTML" },
        //Attribute mappings - END

        postCreate: function() {
            this.inherited(arguments);

            this.own(
                new AnkorStatefulBinding(this.inputName, "value", this.panelRef.append("model.animal.name"), {
                    floodDelay: 200
                }),
                new AnkorStatefulBinding(this, "labelNameStatus", this.panelRef.append("model.nameStatus"))/*,
                new AnkorStatefulBinding(this.inputFamily, "store", this.panelRef.append("model.selectItems.families"), {
                    converter: new AnkorListStoreConverter()
                })*/
            );
        },

        onSubmit: function(e) {
            event.stop(e);
            this.panelRef.append("model").fire("save");
        }
    });
});
