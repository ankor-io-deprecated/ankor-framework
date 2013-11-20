define([
    "./messages/ActionMessage",
    "./messages/ChangeMessage"
], function(ActionMessage, ChangeMessage) {
    var Ref = function(ankorSystem, path) {
        this.ankorSystem = ankorSystem;
        this.segments = [];

        //Parse path into segments
        if (path instanceof Array) {
            this.segments = path;
        }
        else {
            this.segments = this._parsePath(path);
        }
    };

    //////////////////
    // PATH METHODS //
    //////////////////

    Ref.prototype._parsePath = function(path) {
        var segments = [];
        var pathSegments = path.split(".");
        for (var i = 0, pathSegment; (pathSegment = pathSegments[i]); i++) {
            var keyIndex = pathSegment.indexOf("[");
            if (keyIndex == -1) {
                segments.push({
                    type: "property",
                    key: pathSegment
                });
            }
            else {
                var propertyName = pathSegment.substr(0, keyIndex);
                var keys = pathSegment.substring(keyIndex + 1, pathSegment.length - 1).split("][");

                segments.push({
                    type: "property",
                    key: propertyName
                });

                for (var j = 0, key; (key = keys[j]); j++) {
                    if (key.indexOf("'") != -1 || key.indexOf('"') != -1) {
                        segments.push({
                            type: "property",
                            key: key.substring(1, key.length -1)
                        });
                    }
                    else {
                        segments.push({
                            type: "index",
                            key: parseInt(key)
                        });
                    }
                }
            }
        }
        return segments;
    };

    Ref.prototype.path = function() {
        var path = "";
        for (var i = 0, segment; (segment = this.segments[i]); i++) {
            if (segment.type == "property") {
                if (i != 0) {
                    path += ".";
                }
                path += segment.key;
            }
            else if (segment.type == "index") {
                path += "[" + segment.key + "]";
            }
        }
        return path;
    };

    Ref.prototype.append = function(path) {
        var segments = null;

        if (path instanceof Array) {
            segments = path;
        }
        else {
            segments = this._parsePath(path);
        }

        return new Ref(this.ankorSystem, this.segments.concat(segments));
    };

    Ref.prototype.appendIndex = function(index) {
        var segments = this.segments.slice(0);
        segments.push({
            type: "index",
            key: index
        });
        return new Ref(this.ankorSystem, segments);
    };

    Ref.prototype.parent = function() {
        var segments = this.segments.slice(0);
        segments.pop();
        return new Ref(this.ankorSystem, segments);
    };

    Ref.prototype.propertyName = function() {
        return this.segments[this.segments.length - 1].key;
    };

    Ref.prototype.equals = function(ref) {
        return this.path() == ref.path();
    };

    Ref.prototype.isDescendantOf = function(ref) {
        return this.path().indexOf(ref.path()) == 0;
    };

    ///////////////////
    // MODEL METHODS //
    ///////////////////

    Ref.prototype.getValue = function() {
        return this.ankorSystem.model.getValue(this.segments);
    };

    Ref.prototype.setValue = function(value, omitMessage) {
        //Apply value to model
        this.ankorSystem.model.setValue(this.segments, value);

        //Cleanup listeners
        if (value === null) {
            this.ankorSystem.removeInvalidListeners(this.parent());
        }

        //Trigger listeners
        var message = new ChangeMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, null, this.path(), ChangeMessage.prototype.TYPES["VALUE"], null, value);
        this.ankorSystem.triggerListeners(this, message);

        //Send message
        if (!omitMessage) {
            this.ankorSystem.transport.sendMessage(message);
        }
    };

    //Removes this ref from the parent (regardless of map or array)
    Ref.prototype.del = function(omitMessage) {
        //Apply change to model
        this.ankorSystem.model.del(this.segments);

        //Cleanup listeners
        var parentRef = this.parent();
        this.ankorSystem.removeInvalidListeners(parentRef);

        //Trigger listeners
        var message = new ChangeMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, null, parentRef.path(), ChangeMessage.prototype.TYPES["DEL"], this.propertyName(), null);
        this.ankorSystem.triggerListeners(parentRef, message);

        //Send message
        if (!omitMessage) {
            this.ankorSystem.transport.sendMessage(message);
        }
    };

    Ref.prototype.insert = function(index, value, omitMessage) {
        this.ankorSystem.model.insert(this.segments, index, value);

        //Trigger listeners
        var message = new ChangeMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, null, this.path(), ChangeMessage.prototype.TYPES["INSERT"], index, value);
        this.ankorSystem.triggerListeners(this, message);

        //Send message
        if (!omitMessage) {
            this.ankorSystem.transport.sendMessage(message);
        }
    };

    Ref.prototype.size = function() {
        return this.ankorSystem.model.size(this.segments);
    };

    Ref.prototype.isValid = function() {
        return this.ankorSystem.model.isValid(this.segments);
    };

    ///////////////////
    // EVENT METHODS //
    ///////////////////

    Ref.prototype.fire = function(actionName) {
        var message = new ActionMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, null, this.path(), actionName);
        this.ankorSystem.transport.sendMessage(message);
    };

    Ref.prototype.addPropChangeListener = function(cb) {
        return this.ankorSystem.addListener("propChange", this, cb);
    };

    Ref.prototype.addTreeChangeListener = function(cb) {
        return this.ankorSystem.addListener("treeChange", this, cb);
    };

    return Ref;
});
