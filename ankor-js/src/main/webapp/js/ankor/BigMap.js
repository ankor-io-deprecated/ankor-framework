define([
    "./ModelInterface",
    "./Path"
], function(ModelInterface, Path) {
    var BigMap = function(init, model) {
        this.model = model;

        this._cacheSize = 1000;
        this._cacheOrder = [];

        this._loadTimer = null;

        //Parse/Init config
        this._size = init["@size"];
        this._substitute = init["@subst"];

        for (var key in init) {
            if (!init.hasOwnProperty(key) || key == "@size" || key == "@subst") {
                continue;
            }
            this.setValue(new Path("").append(key), init[key]);
        }
    };

    BigMap.prototype = new ModelInterface();

    BigMap.prototype.getValue = function(path) {
        //console.log("BIGMAP getValue", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0) {
            return this.model.getValue(path);
        }
        else {
            var key = path.segments[0].key;

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

    BigMap.prototype.setValue = function(path, value) {
        //console.log("BIGMAP setValue", this.model.baseRef.path.append(path).toString(), value);

        //Throw an error if this is a set on a subproperty for not loaded elements
        var key = path.segments[0].key;
        if (path.segments.length > 1 && !(key in this.model.model)) {
            //throw  new Error("Can't set on BigMap when item not loaded");
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

    BigMap.prototype.isValid = function(path) {
        //console.log("BIGMAP isValid", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0 || path.segments.length == 1) {
            //segments.length == 0 -> isValid for BigMap itself is obviously true
            //segments.length == 1 -> Potentially any key could be contained in the BigMap, so return true as well...
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

    BigMap.prototype.del = function(path) {
        //console.log("BIGMAP del", this.model.baseRef.path.append(path).toString());

        var key = path.segments[0].key;

        if (path.segments.length == 1) {
            //Remove item from model if currently cached
            if (key in this.model.model) {
                delete this.model.model[key];
            }

            //Update size
            this._size--;

            //Remove from @_cacheOrder
            var newCacheOrder = [];
            for (var i = 0; i < this._cacheOrder.length; i++) {
                var cacheKey = this._cacheOrder[i];
                if (cacheKey != key) {
                    newCacheOrder.push(cacheKey);
                }
            }
            this._cacheOrder = newCacheOrder;
        }
        else if (key in this.model.model) {
            //If del for a subpath and item is currently cached -> delegate to model
            this.model.del(path);
        }
    };

    BigMap.prototype.insert = function(path, index, value) {
        //console.log("BIGMAP insert", this.model.baseRef.path.append(path).toString(), index, value);

        if (path.segments.length == 0) {
            throw new Error("Insert only works for Arrays");
        }
        else if (path.segments[0].key in this.model.model) {
            this.model.insert(path, index, value);
        }
    };

    BigMap.prototype.size = function(path) {
        if (path.segments.length == 0) {
            return this._size;
        }
        else {
            return this.model.size(path);
        }
    };

    BigMap.prototype._cleanupCache = function() {
        while (this._cacheOrder.length > this._cacheSize) {
            var key = this._cacheOrder.shift();
            delete this.model.model[key];
        }
    };

    BigMap.prototype._touchCache = function(key) {
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

        //Remove index from cacheOrder array
        this._cacheOrder.splice(index, 1);

        //Re-add at back
        this._cacheOrder.push(key);
    };

    BigMap.prototype._requestMissing = function(key) {
        this.model.baseRef.append(key).fire("@missingProperty");
    };

    return BigMap;
});
