define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/store/Memory"
], function(declare, lang, MemoryStore) {
    /*
     * Special version of an AnkorStatefulBinding especially for dijit.form.Select store bindings.
     *
     * In dojo 1.9 dijit.form.Select can't update the store with set("store", store), but using the setStore method.
     * This special binding takes care of this.
     * Also for now the select values are simply strings in Ankor instead of id/label objects. So this binding also
     * takes care of this. Basically it builds a new Memory store whenever the ref changes.
     */
    return declare(null, {
        constructor: function(select, ref, options) {
            //General members
            this.select = select;
            this.ref = ref;
            lang.mixin(this, options);

            //Handles
            this.ankorHandle = ref.addPropChangeListener(lang.hitch(this, "onAnkorChange"));

            //Init
            this.onAnkorChange();
        },
        onAnkorChange: function(ref, event) {
            //Build data array
            var value = this.ref.getValue();
            var data = [];
            var selectedValue = null;
            for (var i = 0; i < value.length; i++) {
                var id = value[i];
                var label = id;
                if (!id) {
                    id = "";
                    label = " ";
                }
                if (i == 0) {
                    selectedValue = id;
                }

                data.push({
                    id: id,
                    label: label
                });
            }

            //Build memory store
            var store = new MemoryStore({
                data: data
            });

            //Set new store
            this.select.setStore(store);
        },
        remove: function() {
            this.ankorHandle.remove();
        }
    });
});
