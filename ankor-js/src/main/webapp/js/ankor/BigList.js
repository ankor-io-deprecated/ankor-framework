define([
    "./ModelInterface",
    "./Path"
], function(ModelInterface, Path) {
    var BigList = function(config, model) {
        this.model = model;

        this._cacheSize = 1000;
        this._cacheOrder = [];

        this._loadTimer = null;
        this._loadQueue = [];

        //Parse/Init config
        this._size = config["@size"];
        this._chunk = config["@chunk"];
        this._substitute = config["@subst"];

        for (var i = 0; i < config["@init"].length; i++) {
            this.setValue(new Path("").appendIndex(i), config["@init"][i]);
        }
    };

    BigList.prototype = new ModelInterface();

    BigList.prototype.getValue = function(path) {
        //console.log("BIGLIST getValue", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0) {
            var tempArray = [];
            for (var i = 0; i < this._size; i++) {
                tempArray.push(this.getValue([{ type: "index", key: i }]));
            }
            return tempArray;
        }
        else {
            var key = path.segments[0].key;

            if (key >= this._size) {
                throw new Error("Index out of bounds");
            }

            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
                this._requestMissing(key);
            }
            else {
                this._touchCache(key);
            }

            //Run model.getValue
            var value = this.model.getValue(path);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return value;
        }
    };

    BigList.prototype.setValue = function(path, value) {
        //console.log("BIGLIST setValue", this.model.baseRef.path.append(path).toString(), value);

        //Throw an error if this is a set on a subproperty for not loaded elements
        var key = path.segments[0].key;
        if (path.segments.length > 1 && !(key in this.model.model)) {
            //throw  new Error("Can't set on BigList when item not loaded");
            return; //Silently ignore
        }

        //Update cache infos
        if (path.segments.length == 1) {
            if (!(key in this.model.model)) {
                this._cacheOrder.push(key);
            }
            else {
                this._touchCache(key);
            }
        }

        //Set value in model
        this.model.setValue(path, value);

        //Clean up cache
        this._cleanupCache();
    };

    BigList.prototype.isValid = function(path) {
        //console.log("BIGLIST isValid", this.model.baseRef.append(pathSegments).path());

        if (path.segments.length == 0) {
            //isValid for BigList itself -> obviously true
            return true;
        }
        else {
            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var key = path.segments[0].key;
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
            }

            //Run model.isValid
            var valid = this.model.isValid(path);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return valid;
        }
    };

    BigList.prototype.del = function(path) {
        //console.log("BIGLIST del", this.model.baseRef.append(pathSegments).path());

        var key = parseInt(path.segments[0].key);

        if (path.segments.length == 1) {
            //Remove item from model if currently cached
            if (key in this.model.model) {
                delete this.model.model[key];
            }

            //Update size
            this._size--;

            //Update indices of @model
            var newModel = {};
            for (var modelKey in this.model.model) {
                if (!this.model.model.hasOwnProperty(modelKey)) {
                    continue;
                }

                var newKey = parseInt(modelKey);
                if (newKey > key) {
                    newKey--;
                }
                newModel[newKey] = this.model.model[modelKey];
            }
            this.model.model = newModel;

            //Update indices of @_loadQueue
            var newLoadQueue = [];
            for (var i = 0; i < this._loadQueue.length; i++) {
                var loadKey = this._loadQueue[i];
                if (loadKey < key) {
                    newLoadQueue.push(loadKey);
                }
                else if (loadKey > key) {
                    newLoadQueue.push(loadKey - 1);
                }
            }
            this._loadQueue = newLoadQueue;

            //Update indices of @_cacheOrder
            var newCacheOrder = [];
            for (var i = 0; i < this._cacheOrder.length; i++) {
                var cacheKey = this._cacheOrder[i];
                if (cacheKey < key) {
                    newCacheOrder.push(cacheKey);
                }
                else if (cacheKey > key) {
                    newCacheOrder.push(cacheKey - 1);
                }
            }
            this._cacheOrder = newCacheOrder;
        }
        else if (key in this.model.model) {
            //If del for a subpath and item is currently cached -> delegate to model
            this.model.del(path);
        }
    };

    BigList.prototype.insert = function(path, index, value) {
        //console.log("BIGLIST insert", this.model.baseRef.append(pathSegments).path(), index, value);

        index = parseInt(index);

        if (path.segments.length == 0) {
            //Update size
            this._size++;

            //Update indices of @model
            var newModel = {};
            for (var modelKey in this.model.model) {
                if (!this.model.model.hasOwnProperty(modelKey)) {
                    continue;
                }

                var newKey = parseInt(modelKey);
                if (newKey >= index) {
                    newKey++;
                }
                newModel[newKey] = this.model.model[modelKey];
            }
            this.model.model = newModel;

            //Update indices of @_loadQueue
            var newLoadQueue = [];
            for (var i = 0; i < this._loadQueue.length; i++) {
                var loadKey = this._loadQueue[i];
                if (loadKey < index) {
                    newLoadQueue.push(loadKey);
                }
                else if (loadKey >= index) {
                    newLoadQueue.push(loadKey + 1);
                }
            }
            this._loadQueue = newLoadQueue;

            //Update indices of @_cacheOrder
            var newCacheOrder = [];
            for (var i = 0; i < this._cacheOrder.length; i++) {
                var cacheKey = this._cacheOrder[i];
                if (cacheKey < index) {
                    newCacheOrder.push(cacheKey);
                }
                else if (cacheKey >= index) {
                    newCacheOrder.push(cacheKey + 1);
                }
            }
            this._cacheOrder = newCacheOrder;

            //Insert new item
            this.setValue(new Path("").appendIndex(index), value);
        }
        else if (path.segments[0].key in this.model.model) {
            this.model.insert(path, index, value);
        }
    };

    BigList.prototype.size = function(path) {
        if (path.segments.length == 0) {
            return this._size;
        }
        else {
            return this.model.size(path);
        }
    };

    BigList.prototype._cleanupCache = function() {
        while (this._cacheOrder.length > this._cacheSize) {
            var index = this._cacheOrder.shift();
            delete this.model.model[index];
        }
    };

    BigList.prototype._touchCache = function(key) {
        //Find index of key in cacheOrder array
        var index = null;
        if (Array.prototype.indexOf) {
            index = this._cacheOrder.indexOf(key);
        }
        else {
            for (var i = 0; i < this._cacheOrder.length; i++) {
                if (this._cacheOrder[i] == key) {
                    index = i;
                    break;
                }
            }
        }

        //Remove index from cacheOrder arraay
        this._cacheOrder.splice(index, 1);

        //Re-add at back
        this._cacheOrder.push(key);
    };

    BigList.prototype._requestMissing = function(index) {
        this._loadQueue.push(index);
        if (this._loadTimer) {
            clearTimeout(this._loadTimer);
        }
        this._loadTimer = setTimeout(this.model.baseRef.ankorSystem.utils.hitch(this, "_loadMissing"), 100);

        var self = this;
        this._loadTimer = setTimeout(function() {
            //Calc indices that really have to be loaded based on chunk size
            var i;
            var indicesToLoad = [];
            var lastAddedIndex = null;

            //Sort by numerical index
            self._loadQueue.sort(function(lhs, rhs) {
                return lhs - rhs;
            });

            //Step through queued indices from min to max and check if last indicesToLoad entry covers current index with given chunk size
            for (i = 0; i < self._loadQueue.length; i++) {
                var index = self._loadQueue[i];
                if (lastAddedIndex == null || index > lastAddedIndex + self._chunk - 1) {
                    lastAddedIndex = index;
                    indicesToLoad.push(index);
                }
            }

            //Send actions for indicesToLoad
            for (i = 0; i < indicesToLoad.length; i++) {
                self.model.baseRef.appendIndex(indicesToLoad[i]).fire("@missingProperty");
            }

            //Reset loadQueue & timer
            self._loadQueue = [];
            self._loadTimer = null;
        }, 100);
    };

    return BigList;
});
