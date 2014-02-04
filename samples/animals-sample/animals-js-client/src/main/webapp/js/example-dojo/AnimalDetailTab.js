define([
    "dojo/_base/declare",
    "dojo/_base/event",
    "dojo/store/Memory",
    "./BaseTab",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "ankor/adapters/dojo/AnkorDijitSelectBinding",
    "ankor/adapters/dojo/StringConverter",
    "dojo/text!./templates/AnimalDetailTab.html",
    "dijit/form/Form", //Includes only below here
    "dijit/form/TextBox",
    "dijit/form/Select",
    "dijit/form/Button"
], function(declare, event, MemoryStore, BaseTab, AnkorStatefulBinding, AnkorDijitSelectBinding, StringConverter, template) {
    return declare([BaseTab], {
        templateString: template,

        //Attribute mappings - START

        labelNameStatus: "",
        _setLabelNameStatusAttr: { node: "spanNameStatus", type: "innerHTML" },

        //i18n

        labelNameI18n: "",
        _setLabelNameI18nAttr: { node: "spanNameI18n", type: "innerHTML" },

        labelTypeI18n: "",
        _setLabelTypeI18nAttr: { node: "spanTypeI18n", type: "innerHTML" },

        labelFamilyI18n: "",
        _setLabelFamilyI18nAttr: { node: "spanFamilyI18n", type: "innerHTML" },

        //Attribute mappings - END

        postCreate: function() {
            this.inherited(arguments);

            this.own(
                new AnkorStatefulBinding(this.inputName, "value", this.panelRef.append("model.animal.name"), {
                    floodDelay: 200
                }),
                new AnkorStatefulBinding(this, "labelNameStatus", this.panelRef.append("model.nameStatus")),
                new AnkorDijitSelectBinding(this.inputType, this.panelRef.append("model.selectItems.types")),
                new AnkorStatefulBinding(this.inputType, "value", this.panelRef.append("model.animal.type"), {
                    converter: new StringConverter()
                }),
                new AnkorDijitSelectBinding(this.inputFamily, this.panelRef.append("model.selectItems.families")),
                new AnkorStatefulBinding(this.inputFamily, "value", this.panelRef.append("model.animal.family"), {
                    converter: new StringConverter()
                }),

                //i18n
                new AnkorStatefulBinding(this, "labelNameI18n", this.i18nRef.append("Name")),
                new AnkorStatefulBinding(this, "labelTypeI18n", this.i18nRef.append("Type")),
                new AnkorStatefulBinding(this, "labelFamilyI18n", this.i18nRef.append("Family")),
                new AnkorStatefulBinding(this.buttonSave, "label", this.i18nRef.append("Save"))
            );
        },

        onSubmit: function(e) {
            event.stop(e);
            this.panelRef.append("model").fire("save");
        }
    });
});
