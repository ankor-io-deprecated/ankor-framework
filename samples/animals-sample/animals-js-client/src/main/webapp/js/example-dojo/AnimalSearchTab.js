define([
    "dojo/_base/declare",
    "./BaseTab",
    "ankor/adapters/dojo/AnkorStatefulBinding",
    "ankor/adapters/dojo/AnkorListStoreConverter",
    "dgrid/OnDemandGrid",
    "dgrid/extensions/DijitRegistry",
    "dojo/text!./templates/AnimalSearchTab.html"
], function(declare, BaseTab, AnkorStatefulBinding, AnkorListStoreConverter, OnDemandGrid, DijitRegistry, template) {
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
                new AnkorStatefulBinding(this, "title", this.panelRef.append("name")),
                new AnkorStatefulBinding(this.grid, "store", this.panelRef.append("model.animals"), {
                    converter: new AnkorListStoreConverter()
                })
            );
        }
    });
});
