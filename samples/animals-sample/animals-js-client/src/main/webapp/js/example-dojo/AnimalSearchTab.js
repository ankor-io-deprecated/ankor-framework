define([
    "dojo/_base/declare",
    "./BaseTab",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "ankor/adapters/dojo/AnkorDgridBinding",
    "dgrid/OnDemandGrid",
    "dgrid/extensions/DijitRegistry",
    "dojo/text!./templates/AnimalSearchTab.html",
    "dijit/form/TextBox" //Includes only below here
], function(declare, BaseTab, AnkorStatefulBinding, AnkorDgridBinding, OnDemandGrid, DijitRegistry, template) {
    return declare([BaseTab], {
        templateString: template,

        postCreate: function() {
            this.inherited(arguments);

            //Set up table
            var grid = this.grid = new (declare ([OnDemandGrid, DijitRegistry]))({
                columns: {
                    name: {
                        sortable: false,
                        label: "Name"
                    },
                    type: {
                        sortable: false,
                        label: "Type"
                    },
                    family: {
                        sortable: false,
                        label: "Family"
                    }
                }
            }, this.divTable);
            this.grid.startup();
            setTimeout(function() {
                grid.resize();
            }, 0);

            //Bindings
            this.own(
                new AnkorStatefulBinding(this.inputName, "value", this.panelRef.append("model.filter.name"), {
                    floodDelay: 200
                }),
                new AnkorDgridBinding(this.grid, this.panelRef.append("model.animals"))
            );
        }
    });
});
