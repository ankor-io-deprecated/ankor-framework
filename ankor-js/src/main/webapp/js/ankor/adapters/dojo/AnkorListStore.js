define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/store/util/QueryResults"
], function(declare, lang, QueryResults) {
    var resultSetId = 0;

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
            this.observers = {};
            this.listenerHandle = null;
        },

        /////////////
        // HELPERS //
        /////////////
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
        _buildResultSet: function(results, start, count) {
            var store = this;

            var resultSet = QueryResults(results);
            resultSet.total = this.ref.size();
            resultSet._ankorResultsetId_ = resultSetId++;
            resultSet._ankorResultsetStart = start;
            resultSet._ankorResultsetCount = count;

            resultSet.observe = function(listener, includeUpdates) {
                store._registerObserver(this, listener, includeUpdates);
            };
            resultSet.close = function() {
                store._unregisterObserver(this);
            };

            return resultSet;
        },
        _registerObserver: function(resultSet, listener, includeUpdates) {
            console.log("AnkorListStore REGISTEROBSERVER");

            if (!this.listenerHandle) {
                this.listenerHandle = this.ref.addTreeChangeListener(lang.hitch(this, "_onAnkorUpdate"));
            }

            this.observers[resultSet._ankorResultsetId_] = {
                resultSet: resultSet,
                listener: listener,
                includeUpdates: includeUpdates
            };
        },
        _unregisterObserver: function(resultSet) {
            console.log("AnkorListStore UNREGISTEROBSERVER");

            if (resultSet) {
                delete this.observers[resultSet._ankorResultsetId_];
            }

            var openResultSets = 0;
            for (var resultSetId in this.observers) {
                if (this.observers.hasOwnProperty(resultSetId)) {
                    openResultSets++;
                }
            }
            if (openResultSets == 0) {
                this.listenerHandle.remove();
                this.listenerHandle = null;
            }
        },
        _onAnkorUpdate: function(ref, event) {
            console.log("AnkorListStore RESULTSET onAnkorUpdate", ref.path.toString(), event.path.toString(), event)

            if (event.type == event.TYPE.VALUE) {
                if (event.path.equals(this.ref.path)) {
                    //Complete ref changed - this will result in a new store and should not be handled in here
                    this.observers = {};
                    this._unregisterObserver();
                }
                else {
                    //Find out which item has changed
                    var relativeEventPath = event.path.slice(ref.path.segments.length);
                    var elementRef = this.ref.append([relativeEventPath.segments[0]]);
                    var index = elementRef.propertyName();

                    for (var resultSetId in this.observers) {
                        if (!this.observers.hasOwnProperty(resultSetId)) {
                            continue;
                        }
                        var observer = this.observers[resultSetId];
                        if (!observer.includeUpdates) {
                            continue;
                        }
                        if (index < observer.resultSet._ankorResultsetStart || (observer.resultSet._ankorResultsetCount != Infinity && index > observer.resultSet._ankorResultsetStart + observer.resultSet._ankorResultsetCount)) {
                            continue;
                        }
                        var resultIndex = index - observer.resultSet._ankorResultsetStart;
                        var value = elementRef.getValue();
                        var object = this._buildObject(value, index);
                        console.log(object, index, resultIndex);
                        observer.listener(object, resultIndex, resultIndex);
                    }
                }
            }
            else if (event.type == event.TYPE.REPLACE) {
                console.log("REPLACE", event.path.toString())
            }
        },

        ///////////////
        // STORE API //
        ///////////////
        get: function(index) {
            console.log("AnkorListStore GET", index);

            var value = this.ref.appendIndex(index).getValue();
            return this._buildObject(value, index);
        },
        getIdentity: function(object) {
            //console.log("AnkorListStore GETIDENTITY", object, object[this.idProperty]);

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
            var index = start;
            for (var i = 0; i < data.length; i++) {
                results.push(this._buildObject((data[i]), index));
                index++;
            }

            var resultSet = this._buildResultSet(results, start, count);
            console.log("AnkorListStore QUERY RESULT", resultSet);
            return resultSet;
        }
    });
});
