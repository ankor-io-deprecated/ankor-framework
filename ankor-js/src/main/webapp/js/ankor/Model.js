define([
    "./ModelInterface",
    "./BigList"
],function(ModelInterface, BigList) {
    //Class that holds a model, and applies basic operations with a given path (in segments as used in Ref)
    var Model = function(baseRef) {
        this.baseRef = baseRef;
        this.model = {};
    };

    Model.prototype = new ModelInterface();

    Model.prototype.getValue = function(path) {
        //Resolve value in model (or delegate to ModelInterface if encountered during path resolving)
        var value = this.model;
        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for embedded ModelInterface and delegate
            if (value instanceof ModelInterface) {
                return value.getValue(path.slice(i+1));
            }
        }
        return value;
    };

    Model.prototype.setValue = function(path, value) {
        //Resolve parent (and delegate set if ModelInterface is found along the way)
        var parentPath = path.parent();
        var parentModel = this.model;
        for (var i = 0, segment; (segment = parentPath.segments[i]); i++) {
            parentModel = parentModel[segment.key];

            //Check current parentValue
            if (parentModel === undefined || parentModel === null) {
                throw new Error("setValue encountered undefined or null in path");
            }
            //Check for embedded ModelInterface and delegate
            else if (parentModel instanceof ModelInterface) {
                parentModel.setValue(path.slice(i+1), value);
                return;
            }
        }

        //We now have a parentModel (and haven't delegated the set), so apply the value (recursively)
        this._applyValue(parentModel, path.propertyName(), value, path);
    };

    //Helper for setValue to recursively apply values to the model that instantiates BigLists as needed
    Model.prototype._applyValue = function(model, name, value, currentPath) {
        if (value instanceof Array) {
            if (value.length == 1 && (value[0] instanceof Object) && "@chunk" in value[0] && "@init" in value[0] && "@size" in value[0] && "@subst" in value[0]) {
                model[name] = new BigList(value[0], new Model(this.baseRef.append(currentPath)));
            }
            else {
                model[name] = [];
                for (var i = 0; i < value.length; i++) {
                    this._applyValue(model[name], i, value[i], currentPath.appendIndex(i));
                }
            }
        }
        else if (value instanceof Object) {
            model[name] = {};
            for (var key in value) {
                if (!value.hasOwnProperty(key)) {
                    continue;
                }
                this._applyValue(model[name], key, value[key], currentPath.append(key));
            }
        }
        else {
            model[name] = value;
        }
    };

    Model.prototype.isValid = function(path) {
        var valid = true;
        var value = this.model;

        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            //If parent is null (and therefore the current child can't exist) or child is undefined, then set valid to false
            if (value === null || value[segment.key] === undefined) {
                valid = false;
                break;
            }

            //Set new (current) value
            value = value[segment.key];

            //Check for embedded ModelInterface and delegate
            if (value instanceof ModelInterface) {
                return value.isValid(path.slice(i+1));
            }
        }

        return valid;
    };

    Model.prototype.del = function(path) {
        //Resolve parent (and delegate del if ModelInterface is found along the way)
        var parentPath = path.parent();
        var parentModel = this.model;
        for (var i = 0, segment; (segment = parentPath.segments[i]); i++) {
            parentModel = parentModel[segment.key];

            //Check current parentValue
            if (parentModel === undefined || parentModel === null) {
                throw new Error("del encountered undefined or null in path");
            }
            //Check for embedded ModelInterface and delegate
            else if (parentModel instanceof ModelInterface) {
                parentModel.del(path.slice(i+1));
                return;
            }
        }

        //We haven't delegated to ModelInterface, so now remove the value
        var lastSegment = path.segments[path.segments.length - 1];
        if (parentModel instanceof Array) {
            parentModel.splice(lastSegment.key, 1);
        }
        else {
            delete parentModel[lastSegment.key];
        }
    };

    Model.prototype.insert = function(path, index, insertValue) {
        var value = this.model;
        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for embedded ModelInterface and delegate
            if (value instanceof ModelInterface) {
                value.insert(path.slice(i+1), index, insertValue);
                return;
            }
        }

        if (!(value instanceof Array)) {
            throw new Error("Insert only works for Arrays");
        }

        value.splice(index, 0, insertValue);
    };

    Model.prototype.size = function(path) {
        var value = this.model;

        //Resolve value and delegate if BigList
        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            if (value != undefined) {
                value = value[segment.key];
            }

            //Check for embedded ModelInterface and delegate
            if (value instanceof ModelInterface) {
                return value.size(path.slice(i+1));
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