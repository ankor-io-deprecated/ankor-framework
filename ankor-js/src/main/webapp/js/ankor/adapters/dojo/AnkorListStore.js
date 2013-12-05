define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/store/util/QueryResults"
], function(declare, lang, QueryResults) {
    /*
     * Simple ankor list (array or BigList) wrapper. Will ignore all query options besides "start" and "count".
     * If the value of the row is an object it will be enhanced by an _ankorListIndex_ property as dojo store id.
     * If the value is something else (string, ...) the store will return an object with the _ankorListIndex_ id,
     * and a value property.
     */
    return declare(null, {
        idProperty: "_ankorListIndex_",

        constructor: function(ref) {
            this.ref = ref;
        },
        _buildObject: function(value, index) {
            var object = null;
            if (value instanceof Object && !(value instanceof Array)) {
                object = lang.clone(value);
            }
            else {
                object = {
                    value: lang.clone(value)
                };
            }
            object[this.idProperty] = index;
            return object;
        },
        get: function(index) {
            console.log("AnkorListStore GET", index);

            var value = this.ref.appendIndex(index).getValue();
            return this._buildObject(value, index);
        },
        getIdentity: function(object) {
            console.log("AnkorListStore GETIDENTITY", object, object[this.idProperty]);

            return object[this.idProperty];
        },
        query: function(query, options) {
            console.log("AnkorListStore QUERY", query, options);

            //Get start & count options
            options = options || {};
            var start = options.start || 0;
            var count = options.count || Infinity;

            //Build data array
            var data = [];
            if (start === 0 && count === Infinity) {
                data = this.ref.getValue();
            }
            else {
                for (var i = 0; i < count; i++) {
                    data.push(this.ref.appendIndex(i + start).getValue());
                }
            }

            //Build result
            var results = [];
            for (var i = start; i < data.length + start; i++) {
                results.push(this._buildObject(data[i], i));
            }
            results = QueryResults(results);
            results.total = this.ref.size();

            console.log("AnkorListStore QUERY RESULT", results);

            return results;
        }
    });
});
