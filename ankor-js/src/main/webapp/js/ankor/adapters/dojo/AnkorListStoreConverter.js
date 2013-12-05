define([
    "dojo/_base/declare",
    "./AnkorListStore"
], function(declare, AnkorListStore) {
    return declare(null, {
        fromAnkor: function(ref) {
            return new AnkorListStore(ref);
        },
        toAnkor: function(ref, value) {
            //NOP
        }
    });
});
