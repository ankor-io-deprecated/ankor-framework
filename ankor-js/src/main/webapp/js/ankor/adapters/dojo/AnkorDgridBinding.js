define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "./AnkorListStore"
], function(declare, lang, AnkorListStore) {
    /*
     * Special version of an AnkorStatefulBinding especially for DGrids - to properly clean up all bindings
     * when the whole store gets replaced. This is necessary because DGrid doesn't properly close all ResulSet
     * observes when replacing the store.
     *
     * THIS WOULDN'T BE NECESSARY IF THIS WOULD BE CLEANLY IMPLEMENTED IN DGRID - REMOVE THIS CLASS IF THIS EVER CHANGES
     */
    return declare(null, {
        constructor: function(dgrid, ref, options) {
            //General members
            this.dgrid = dgrid;
            this.ref = ref;
            lang.mixin(this, options);

            //Handles
            this.ankorHandle = ref.addPropChangeListener(lang.hitch(this, "onAnkorChange"));

            //Init
            this.onAnkorChange();
        },
        onAnkorChange: function(ref, event) {
            //Don't handle this event if it's not for this exact path OR if it's a REPLACE event
            /*
             * REPLACE events are handled within the store, all other (VALUE of the ref itself changed, or DEL or INSERT)
             * should result in a new store. This is a hack that works best with the current Dgrid/Observable store
             * problems. See: https://github.com/SitePen/dgrid/issues/714
             *
             * There are multiple problems with inconsistencies between Ankor and Dojo Store concepts.
             * Additionally there are "internal" problems between DGrid and Dojo Store concepts.
             * As it seems, it's not possible to implement a really working delete/add of rows with Store/Ankor.
             * Therefore the savest way is to simply set a new store when the size changes and refresh the table.
             * This has multiple reasons:
             * 1. We have no real id for every store object. We fake it using the index of the Ankor list. So if we
             *    delete row 0 (id 0) row 1 becomes row 0 with the id 0 -> problem. We have to fake the id though,
             *    because with BigLists substitutes we simply have no IDs...
             * 2. We don't use put/add to change the store (like this we could use the "Observable" wrapper dojo supplies.
             *    But again, we don't have unique IDs for table rows, so that doesn't work with the subst...
             * 3. If we wouldn't use subst for the BigList, but promises that later resolve, it probably would kind of work (better).
             */
            if (event && (!event.path.equals(this.ref.path) || event.type == event.TYPE.REPLACE)) {
                return;
            }

            this.destroyStore(); //Destroy previous store
            this.dgrid.set("store", new AnkorListStore(this.ref));
        },
        destroyStore: function() {
            var store = this.dgrid.get("store");
            if (store) {
                store.destroy();
            }
        },
        remove: function() {
            this.destroyStore();
            this.ankorHandle.remove();
        }
    });
});
