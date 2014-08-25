define([
    "./ModelInterface",
    "./Path",
    "./BigCacheController"
], function(ModelInterface, Path, BigCacheController) {
    var BigMap = function(init, model) {
        this.model = model;
        this.cacheController = new BigCacheController(model);

        this._loadTimer = null;
        this._loadPending = {};

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
                this.cacheController.touch(key);
            }

            //Run model.getValue
            var value = this.model.getValue(path);

            //Delete substitute if getValue was run against subst
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

        //Update internal state
        if (path.segments.length == 1) {
            //Update cache
            if (!(key in this.model.model)) {
                this.cacheController.add(key);
            }
            else {
                this.cacheController.touch(key);
            }

            //Update _loadPending
            delete this._loadPending[key];
        }

        //Set value in model
        this.model.setValue(path, value);

        //Clean up cache
        this.cacheController.cleanup();
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

            //Remove from cache
            this.cacheController.remove(key);
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

    BigMap.prototype._requestMissing = function(key) {
        if (this._loadPending[key]) {
            return;
        }

        this._loadPending[key] = true;
        this.model.baseRef.append(key).fire("@missingProperty");
    };

    return BigMap;
});
