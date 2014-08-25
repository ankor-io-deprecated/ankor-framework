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
            this.observedResultSets = {};
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
            resultSet._ankorResultSetId = resultSetId++;
            resultSet._ankorResultSetStart = start;
            resultSet._ankorResultSetCount = count;

            resultSet.observe = function(listener, includeUpdates) {
                store._registerObservedResultSetListener(this, listener, includeUpdates);

                //While the Dojo documentation says that resultSet.close should be used - it seems that only a handle returned by observe is used for this...
                var handle = {};
                handle.remove = handle.cancel = function() {
                    store._unregisterObservedResultSetListener(resultSet, listener);
                };
                return handle;
            };
            resultSet.close = function() {
                store._unregisterObservedResultSet(this);
            };

            return resultSet;
        },
        _registerObservedResultSetListener: function(resultSet, listenerCallback, includeUpdates) {
            if (!this.listenerHandle) {
                this.listenerHandle = this.ref.addTreeChangeListener(lang.hitch(this, "_onAnkorUpdate"));
            }

            if (!(resultSet._ankorResultSetId in this.observedResultSets)) {
                this.observedResultSets[resultSet._ankorResultSetId] = {
                    resultSet: resultSet,
                    listeners: []
                }
            }

            this.observedResultSets[resultSet._ankorResultSetId].listeners.push({
                callback: listenerCallback,
                includeUpdates: includeUpdates
            });
        },
        _unregisterObservedResultSet: function(resultSet) {
            if (resultSet && resultSet._ankorResultSetId in this.observedResultSets) {
                delete this.observedResultSets[resultSet._ankorResultSetId];
            }
            this._cleanupListenerHandle();
        },
        _unregisterObservedResultSetListener: function(resultSet, listenerCallback) {
            if (resultSet && resultSet._ankorResultSetId in this.observedResultSets) {
                var listeners = this.observedResultSets[resultSet._ankorResultSetId].listeners.slice();
                var filteredListeners = [];

                for (var i = 0; i < listeners.length; i++) {
                    if (listeners[i].callback !== listenerCallback) {
                        filteredListeners.push(listeners[i]);
                    }
                }

                if (filteredListeners.length == 0) {
                    delete this.observedResultSets[resultSet._ankorResultSetId];
                }
                else {
                    this.observedResultSets[resultSet._ankorResultSetId].listeners = filteredListeners;
                }
            }
            this._cleanupListenerHandle();
        },
        _cleanupListenerHandle: function() {
            if (!this.listenerHandle) {
                return;
            }

            var observedResultSetCount = 0;
            for (var resultSetId in this.observedResultSets) {
                if (this.observedResultSets.hasOwnProperty(resultSetId)) {
                    observedResultSetCount++;
                }
            }
            if (observedResultSetCount == 0) {
                this.listenerHandle.remove();
                this.listenerHandle = null;
            }
        },
        _onAnkorUpdate: function(ref, event) {
            var index;

            //Only handle the event if it's within a child (all event types)
            if (!event.path.equals(this.ref.path)) {
                index = event.path.segments[ref.path.segments.length].key;
                this._notifyObserverUpdate(index);
            }
            //Or it's a replace event
            else if (event.type == event.TYPE.REPLACE) {
                index = event.key;
                for (var i = 0; i < event.value.length; i++) {
                    this._notifyObserverUpdate(index);
                    index++;
                }
            }
        },
        _notifyObserverUpdate: function(index) {
            for (var resultSetId in this.observedResultSets) {
                if (!this.observedResultSets.hasOwnProperty(resultSetId)) {
                    continue;
                }

                var observedResultSet = this.observedResultSets[resultSetId];

                //Check if resultSet covers this index
                if (index < observedResultSet.resultSet._ankorResultSetStart || (observedResultSet.resultSet._ankorResultSetCount != Infinity && index >= observedResultSet.resultSet._ankorResultSetStart + observedResultSet.resultSet._ankorResultSetCount)) {
                    continue;
                }

                //Update object in resultSet
                var elementRef = this.ref.appendIndex(index);
                var resultIndex = index - observedResultSet.resultSet._ankorResultSetStart;
                var value = elementRef.getValue();
                var object = this._buildObject(value, index);
                observedResultSet.resultSet[resultIndex] = object;

                //Notify listeners
                for (var i = 0; i < observedResultSet.listeners.length; i++) {
                    var listener = observedResultSet.listeners[i];
                    if (!listener.includeUpdates) {
                        continue;
                    }
                    listener.callback(object, resultIndex, resultIndex);
                }
            }
        },

        ///////////
        //EXTRA API
        ///////////
        destroy: function() {
            this.observedResultSets = {};
            this._cleanupListenerHandle();
        },

        ///////////////
        // STORE API //
        ///////////////
        get: function(index) {
            var value = this.ref.appendIndex(index).getValue();
            return this._buildObject(value, index);
        },
        getIdentity: function(object) {
            return object[this.idProperty];
        },
        query: function(query, options) {
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
                var size = this.ref.size();
                if (count === Infinity || start + count > size) {
                    count = this.ref.size() - start;
                }
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

            return this._buildResultSet(results, start, count);
        },
        put: function(object, directives) {
            var index = this.getIdentity(object);
            var elementRef = this.ref.appendIndex(index);
            var oldValue = elementRef.getValue();

            if (oldValue instanceof Object && !(oldValue instanceof Array)) {
                for (var property in oldValue) {
                    if (!oldValue.hasOwnProperty(property)) {
                        continue;
                    }
                    if (object[property] !== oldValue[property]) {
                        elementRef.append(property).setValue(object[property], this);
                    }
                }
            }
            else {
                if (object.value != oldValue) {
                    elementRef.setValue(object.value, this);
                }
            }
        }
    });
});
