define([
    "./Ref",
    "./utils"
], function(Ref, utils) {
    var listenerCounter = 0;

    var AnkorSystem = function(options) {
        this.senderId = utils.uuid();
        this.modelId = options.modelId || utils.uuid();
        this.transport = options.transport;
        this.model = {};
        this.propListeners = {};
        this.treeListeners = {};

        if (!this.transport) {
            throw new Error("AnkorSystem missing transport");
        }
        else {
            this.transport.setMessageHandler(utils.hitch(this, "processIncomingMessage"));
        }
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
            remove: utils.hitch(this, function() {
                delete listeners[path].listeners[listenerId];
            })
        };
    };

    AnkorSystem.prototype.triggerListeners = function(ref) {
        //Trigger propChangeListeners
        var path = ref.path();
        var listeners = this.propListeners[path];
        if (listeners) {
            for (var id in listeners.listeners) {
                var listener = listeners.listeners[id];
                listener(ref);
            }
        }

        //Trigger TreeChangeListeners
        var treeRef = ref;
        while (path) {
            var listeners = this.treeListeners[path];
            if (listeners) {
                for (var id in listeners.listeners) {
                    var listener = listeners.listeners[id];
                    listener(ref);
                }
            }
            treeRef = treeRef.parent();
            path = treeRef.path();
        }

        //Recursion for child properties
        var value = ref.getValue();
        if (value instanceof Array) {
            for (var i = 0; (i < value.length); i++) {
                this.triggerListeners(ref.appendIndex(i));
            }
        }
        else if (typeof value == "object") {
            for (var propertyName in value) {
                this.triggerListeners(ref.append(propertyName));
            }
        }
    };

    AnkorSystem.prototype.removeListeners = function(ref) {
        var filterObsoleteListeners = function(listeners) {
            for (var path in listeners) {
                var listener = listeners[path];
                if (listener.ref.equals(ref) || listener.ref.isDescendantOf(ref)) {
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
        ref.setValue(message.value, true);

    };

    return AnkorSystem;
});
