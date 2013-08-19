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
            var pathSegments = path.split(".");
            for (var i = 0, pathSegment; (pathSegment = pathSegments[i]); i++) {
                var keyIndex = pathSegment.indexOf("[");
                if (keyIndex == -1) {
                    this.segments.push({ property: pathSegment });
                }
                else {
                    var propertyName = pathSegment.substr(0, keyIndex);
                    var keys = pathSegment.substring(keyIndex + 1, pathSegment.length - 1).split("][");

                    this.segments.push({ property: propertyName });

                    for (var j = 0, key; (key = keys[j]); j++) {
                        if (key.indexOf("'") != -1 || key.indexOf('"') != -1) {
                            this.segments.push({ property: key.substring(1, key.length -1) });
                        }
                        else {
                            this.segments.push({ index: parseInt(key) });
                        }
                    }
                }
            }
        }
    };

    Ref.prototype.path = function() {
        var path = "";
        for (var i = 0, segment; (segment = this.segments[i]); i++) {
            if (segment.property != undefined) {
                if (i != 0) {
                    path += ".";
                }
                path += segment.property;
            }
            else if (segment.index != undefined) {
                path += "[" + segment.index + "]";
            }
        }
        return path;
    };

    Ref.prototype.append = function(path) {
        return new Ref(this.ankorSystem, this.path() + "." + path);
    };

    Ref.prototype.appendIndex = function(index) {
        var segments = this.segments.slice(0);
        segments.push({ index: index });
        return new Ref(this.ankorSystem, segments);
    };

    Ref.prototype.getValue = function() {
        var value = this.ankorSystem.model;
        for (var i = 0, segment; (segment = this.segments[i]); i++) {
            if (segment.property != undefined) {
                value = value[segment.property];
            }
            else if (segment.index != undefined) {
                value = value[segment.index];
            }
        }
        return value;
    };

    Ref.prototype.setValue = function(value, omitMessage) {
        var model = this.parent().getValue();
        model[this.propertyName()] = value;
        this.ankorSystem.triggerListeners(this);
        if (!omitMessage) {
            var message = new ChangeMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, null, this.path(), value);
            this.ankorSystem.transport.sendMessage(message);
        }
        if (value == null) {
            this.ankorSystem.removeListeners(this);
        }
    };

    Ref.prototype.parent = function() {
        var segments = this.segments.slice(0);
        segments.pop();
        return new Ref(this.ankorSystem, segments);
    };

    Ref.prototype.propertyName = function() {
        var segment = this.segments[this.segments.length - 1];
        if (segment.property != undefined) {
            return segment.property;
        }
        else if (segment.index != undefined) {
            return segment.index;
        }
    }

    Ref.prototype.equals = function(ref) {
        return this.path() == ref.path();
    }

    Ref.prototype.isDescendantOf = function(ref) {
        return this.path().indexOf(ref.path()) == 0;
    }

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
