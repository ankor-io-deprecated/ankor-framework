define([
    "./Ref"
], function(Ref) {
    var listenerCounter = 0;

    var AnkorSystem = function(options) {
        if (!options.utils) {
            throw new Error("AnkorSystem missing utils");
        }
        if (!options.transport) {
            throw new Error("AnkorSystem missing transport");
        }

        this.debug = options.debug || false;
        this.utils = options.utils;
        this.senderId = this.utils.uuid();
        this.modelId = options.modelId || this.utils.uuid();
        this.transport = options.transport;
        this.model = {};
        this.propListeners = {};
        this.treeListeners = {};

        this.transport.init(this);
    };

    AnkorSystem.prototype.getRef = function(path) {
        return new Ref(this, path);
    };

    AnkorSystem.prototype.addListener = function(type, ref, cb) {
        var listeners = null;
        if (type == "propChange") {
            listeners = this.propListeners;
        }
        else if (type == "treeChange") {
            listeners = this.treeListeners;
        }
        var path = ref.path();
        if (!(path in listeners)) {
            listeners[path] = {
                ref: ref,
                listeners: {}
            };
        }
        var listenerId = "#" + listenerCounter++;
        listeners[path].listeners[listenerId] = cb;
        return {
            remove: this.utils.hitch(this, function() {
                delete listeners[path].listeners[listenerId];
            })
        };
    };

    AnkorSystem.prototype.triggerListeners = function(ref, message) {
        //Trigger propChangeListeners
        var path = ref.path();
        var listeners = this.propListeners[path];
        if (listeners) {
            for (var id in listeners.listeners) {
                var listener = listeners.listeners[id];
                listener(ref, message);
            }
        }

        //Trigger TreeChangeListeners
        var treeRef = ref;
        while (path) {
            var listeners = this.treeListeners[path];
            if (listeners) {
                for (var id in listeners.listeners) {
                    var listener = listeners.listeners[id];
                    listener(ref, message);
                }
            }
            treeRef = treeRef.parent();
            path = treeRef.path();
        }

        //Recursion for child properties
        var value = ref.getValue();
        if (value instanceof Array) {
            for (var i = 0; (i < value.length); i++) {
                this.triggerListeners(ref.appendIndex(i), message);
            }
        }
        else if (typeof value == "object") {
            for (var propertyName in value) {
                this.triggerListeners(ref.append(propertyName), message);
            }
        }
    };

    //Removes all listeners that are descendants of the given ref that are no longer valid (for a ref that points nowhere)
    AnkorSystem.prototype.removeInvalidListeners = function(ref) {
        var filterObsoleteListeners = function(listeners) {
            for (var path in listeners) {
                var listener = listeners[path];
                if (listener.ref.isDescendantOf(ref) && !listener.ref.isValid()) {
                    listener.listeners = null;
                    delete listeners[path];
                }
            }
        };

        filterObsoleteListeners(this.propListeners);
        filterObsoleteListeners(this.treeListeners);
    };

    AnkorSystem.prototype.processIncomingMessage = function(message) {
        var ref = this.getRef(message.property);

        if (message.type == message.TYPES["VALUE"]) {
            ref.setValue(message.value, true);
        }
        else if (message.type == message.TYPES["DEL"]) {
            if (ref.getValue() instanceof Array) {
                ref.appendIndex(message.key).del(true);
            }
            else {
                ref.append(message.key).del(true);
            }
        }
        else if (message.type == message.TYPES["INSERT"]) {
            ref.insert(message.key, message.value, true);
        }
        else if (message.type == message.TYPES["REPLACE"]) {
            for(var i = 0; i < message.value.length; i++) {
                ref.appendIndex(message.key + i).setValue(message.value[i]);
            }
        }
    };

    return AnkorSystem;
});
