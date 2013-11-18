define([
    "./BigList"
],function(BigList) {
    //Class that holds a model, and applies basic operations with a given path (in segments as used in Ref)
    var Model = function(baseRef) {
        this.baseRef = baseRef;
        this.model = {};
    };

    Model.prototype.getValue = function(pathSegments) {
        //Resolve value in model (or delegate to BigList if encountered during path resolving)
        var value = this.model;
        for (var i = 0, segment; (segment = pathSegments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for BigList
            if (value instanceof BigList) {
                return value.getValue(pathSegments.slice(i+1, pathSegments.length));
            }
        }
        return value;
    };

    Model.prototype.setValue = function(pathSegments, value) {
        //Resolve parent (and delegate set if BigList is found along the way)
        var propertyName = pathSegments[pathSegments.length - 1].key;
        var parentPathSegments = pathSegments.slice(0, -1);
        var parentModel = this.model;
        for (var i = 0, segment; (segment = parentPathSegments[i]); i++) {
            parentModel = parentModel[segment.key];

            //Check current parentValue
            if (parentModel === undefined || parentModel === null) {
                throw new Error("setValue encountered undefined or null in path");
            }
            else if (parentModel instanceof BigList) {
                var remainingSegments = pathSegments.slice(i+1, pathSegments.length);
                parentModel.setValue(remainingSegments, value);
                return;
            }
        }

        //We now have a parentModel (and haven't delegated the set), so apply the value (recursively)
        this._applyValue(parentModel, propertyName, value, pathSegments);
    };

    //Helper for setValue to recursively apply values to the model that instantiates BigLists as needed
    Model.prototype._applyValue = function(model, name, value, currentPathSegments) {
        if (value instanceof Array) {
            if (value.length == 1 && (value[0] instanceof Object) && "@chunk" in value[0] && "@init" in value[0] && "@size" in value[0] && "@subst" in value[0]) {
                model[name] = new BigList(value[0], new Model(this.baseRef.append(currentPathSegments)));
            }
            else {
                model[name] = [];
                for (var i = 0; i < value.length; i++) {
                    this._applyValue(model[name], i, value[i], currentPathSegments.concat([{
                        type: "index",
                        key: i
                    }]));
                }
            }
        }
        else if (value instanceof Object) {
            model[name] = {};
            for (var key in value) {
                this._applyValue(model[name], key, value[key], currentPathSegments.concat([{
                    type: "property",
                    key: key
                }]));
            }
        }
        else {
            model[name] = value;
        }
    };

    Model.prototype.isValid = function(pathSegments) {
        var valid = true;
        var value = this.model;

        for (var i = 0, segment; (segment = pathSegments[i]); i++) {
            //If parent is null (and therefore the current child can't exist) or child is undefined, then set valid to false
            if (value === null || value[segment.key] === undefined) {
                valid = false;
                break;
            }

            //Set new (current) value
            value = value[segment.key];

            //Check for BigList
            if (value instanceof BigList) {
                return value.isValid(pathSegments.slice(i+1, pathSegments.length));
            }
        }

        return valid;
    };

    Model.prototype.del = function(pathSegments) {
        //Resolve parent (and delegate del if BigList is found along the way)
        var parentPathSegments = pathSegments.slice(0, -1);
        var parentModel = this.model;
        for (var i = 0, segment; (segment = parentPathSegments[i]); i++) {
            parentModel = parentModel[segment.key];

            //Check current parentValue
            if (parentModel === undefined || parentModel === null) {
                throw new Error("del encountered undefined or null in path");
            }
            else if (parentModel instanceof BigList) {
                parentModel.del(pathSegments.slice(i+1, pathSegments.length));
                return;
            }
        }

        //We haven't delegated to BigList, so now remove the value
        var lastSegment = pathSegments[pathSegments.length - 1];
        if (lastSegment.type == "property") {
            delete parentModel[lastSegment.key];
        }
        else if (lastSegment.type == "index") {
            parentModel.splice(lastSegment.key, 1);
        }
    };

    Model.prototype.insert = function(pathSegments, index, value) {
        var value = this.model;
        for (var i = 0, segment; (segment = pathSegments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for BigList
            if (value instanceof BigList) {
                value.insert(pathSegments.slice(i+1, pathSegments.length), index, value);
            }
        }

        if (!(value instanceof Array)) {
            throw new Error("Insert only works for Arrays");
        }

        value.splice(index, 0, value);
    };

    Model.prototype.size = function(pathSegments) {
        var value = this.model;

        //Resolve value and delegate if BigList
        for (var i = 0, segment; (segment = pathSegments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for BigList
            if (value instanceof BigList) {
                return value.size(pathSegments.slice(i+1, pathSegments.length));
            }
        }

        //Check size of resolved value if not delegated
        if (value instanceof Array) {
            return value.length;
        }
        else if (value instanceof Object) {
            var count = 0;
            for (var key in value) {
                count++;
            }
            return count;
        }
        else {
            return -1;
        }
    };

    return Model;
});