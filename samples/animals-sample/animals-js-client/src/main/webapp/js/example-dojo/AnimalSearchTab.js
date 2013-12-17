define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "./BaseTab",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "ankor/adapters/dojo/AnkorDgridBinding",
    "ankor/adapters/dojo/AnkorDijitSelectBinding",
    "ankor/adapters/dojo/StringConverter",
    "dijit/registry",
    "dijit/form/Button",
    "dgrid/OnDemandGrid",
    "dgrid/extensions/DijitRegistry",
    "dgrid/editor",
    "dojo/text!./templates/AnimalSearchTab.html",
    "dijit/form/TextBox", //Includes only below here
    "dijit/form/Select"
], function(declare, lang, aspect, BaseTab, AnkorStatefulBinding, AnkorDgridBinding, AnkorDijitSelectBinding, StringConverter, registry, Button, OnDemandGrid, DijitRegistry, dgridEditor, template) {
    return declare([BaseTab], {
        templateString: template,

        //Attribute mappings - START
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

            //Set up table
            var grid = this.grid = new (declare ([OnDemandGrid, DijitRegistry]))({
                columns: [
                    dgridEditor({
                        field: "name",
                        label: "Name",
                        sortable: false,
                        editor: "text",
                        editOn: "dblclick",
                        autoSave: true
                    }),
                    {
                        field: "type",
                        label: "Type",
                        sortable: false
                    },
                    {
                        field: "family",
                        label: "Family",
                        sortable: false
                    },
                    {
                        field: "uuid",
                        label: "",
                        sortable: false,
                        renderCell: lang.hitch(this, function(object, value, node, options) {
                            if (object.name == "...") {
                                //this is a subst
                                return;
                            }

                            var editButton = new Button({
                                onClick: lang.hitch(this, function() {
                                    this.panelRef.append("model").fire("edit", {
                                        uuid: value
                                    })
                                })
                            });
                            editButton.own(
                                new AnkorStatefulBinding(editButton, "label", this.i18nRef.append("Edit"))
                            );
                            editButton.startup();
                            node.appendChild(editButton.domNode);

                            var deleteButton = new Button({
                                onClick: lang.hitch(this, function() {
                                    this.panelRef.append("model").fire("delete", {
                                        uuid: value
                                    })
                                })
                            });
                            deleteButton.own(
                                new AnkorStatefulBinding(deleteButton, "label", this.i18nRef.append("Delete"))
                            );
                            deleteButton.startup();
                            node.appendChild(deleteButton.domNode);
                        })
                    }
                ]
            }, this.divTable);
            aspect.before(this.grid, "removeRow", function(rowElement) {
                var widgets = registry.findWidgets(rowElement);
                for (var i = 0, widget; (widget = widgets[i]); i++) {
                    widget.destroyRecursive();
                }
            });
            this.grid.startup();
            setTimeout(function() {
                grid.resize();
            }, 0);

            //Bindings
            this.own(
                new AnkorStatefulBinding(this.inputName, "value", this.panelRef.append("model.filter.name"), {
                    floodDelay: 200
                }),
                new AnkorDgridBinding(this.grid, this.panelRef.append("model.animals")),
                new AnkorDijitSelectBinding(this.inputType, this.panelRef.append("model.selectItems.types")),
                new AnkorStatefulBinding(this.inputType, "value", this.panelRef.append("model.filter.type"), {
                    converter: new StringConverter()
                }),
                new AnkorDijitSelectBinding(this.inputFamily, this.panelRef.append("model.selectItems.families")),
                new AnkorStatefulBinding(this.inputFamily, "value", this.panelRef.append("model.filter.family"), {
                    converter: new StringConverter()
                }),

                //i18n
                new AnkorStatefulBinding(this, "labelNameI18n", this.i18nRef.append("Name")),
                new AnkorStatefulBinding(this, "labelTypeI18n", this.i18nRef.append("Type")),
                new AnkorStatefulBinding(this, "labelFamilyI18n", this.i18nRef.append("Family")),
                new AnkorStatefulBinding(this.buttonSave, "label", this.i18nRef.append("Save")),
                this.i18nRef.append("Name").addPropChangeListener(lang.hitch(this, "updateColumnHeaders")),
                this.i18nRef.append("Type").addPropChangeListener(lang.hitch(this, "updateColumnHeaders")),
                this.i18nRef.append("Family").addPropChangeListener(lang.hitch(this, "updateColumnHeaders"))
            );
        },
        onSave: function() {
            this.panelRef.append("model").fire("save");
        },
        updateColumnHeaders: function() {
            this.grid.columns[0].label = this.i18nRef.append("Name").getValue();
            this.grid.columns[1].label = this.i18nRef.append("Type").getValue();
            this.grid.columns[2].label = this.i18nRef.append("Family").getValue();
            this.grid._updateColumns();
        }
    });
});
