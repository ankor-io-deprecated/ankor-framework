define(function() {
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
            this.setValue([{ type: "index", key: i }], config["@init"][i]);
        }
    };

    BigList.prototype.setValue = function(pathSegments, value) {
        //console.log("BIGLIST setValue", this.model.baseRef.append(pathSegments).path(), value);

        //Throw an error if this is a set on a subproperty for not loaded elements
        var key = pathSegments[0].key;
        if (pathSegments.length > 1 && !(key in this.model.model)) {
            throw  new Error("Can't set on BigList when item not loaded");
        }

        //Update cache infos
        if (pathSegments.length == 1) {
            if (!(key in this.model.model)) {
                this._cacheOrder.push(key);
            }
        }

        //Set value in model
        this.model.setValue(pathSegments, value);

        //Clean up cache
        this._cleanupCache();
    };

    BigList.prototype.getValue = function(pathSegments) {
        //console.log("BIGLIST getValue", this.model.baseRef.append(pathSegments).path());

        if (pathSegments.length == 0) {
            var tempArray = [];
            for (var i = 0; i < this._size; i++) {
                tempArray.push(this.getValue([{ type: "index", key: i }]));
            }
            return tempArray
        }
        else {
            var key = pathSegments[0].key;

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

            //Run model.getValue
            var value = this.model.getValue(pathSegments);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return value;
        }
    };

    BigList.prototype.isValid = function(pathSegments) {
        //console.log("BIGLIST isValid", this.model.baseRef.append(pathSegments).path());

        if (pathSegments.length == 0) {
            //isValid for BigList itself -> obviously true
            return true;
        }
        else {
            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var key = pathSegments[0].key;
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
            }

            //Run model.isValid
            var valid = this.model.isValid(pathSegments);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return valid;
        }
    };

    BigList.prototype.del = function(pathSegments) {
        //console.log("BIGLIST del", this.model.baseRef.append(pathSegments).path());

        throw new Error("BigList.del not implemented yet");
    };

    BigList.prototype.insert = function(pathSegments, index, value) {
        //console.log("BIGLIST insert", this.model.baseRef.append(pathSegments).path(), index, value);

        throw new Error("BigList.insert not implemented yet");
    };

    BigList.prototype.size = function(pathSegments) {
        if (pathSegments.length == 0) {
            return this._size;
        }
        else {
            return this.model.size(pathSegments);
        }
    };

    BigList.prototype._cleanupCache = function() {
        //Todo: Would this need events for changed properties? -> Probably yes since it's like setting the subst again
    };

    BigList.prototype._requestMissing = function(index) {
        this._loadQueue.push(index);
        if (this._loadTimer) {
            clearTimeout(this._loadTimer);
        }
        this._loadTimer = setTimeout(this.model.baseRef.ankorSystem.utils.hitch(this, "_loadMissing"), 100);
    };

    BigList.prototype._loadMissing = function() {
        //Calc indices that really have to be loaded based on chunk size
        var indicesToLoad = {};
        for (var i = 0; i < this._loadQueue.length; i++) {
            var index = this._loadQueue[i];
            var baseIndex = Math.floor(index / this._chunk) * this._chunk;
            indicesToLoad[baseIndex] = true;
        }

        //Send actions for indicesToLoad
        for (index in indicesToLoad) {
            this.model.baseRef.appendIndex(index).fire("@missingProperty");
        }

        //Reset loadQueue & timer
        this._loadQueue = [];
        this._loadTimer = null;
    };

    return BigList;
});
