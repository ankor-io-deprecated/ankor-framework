define(function() {
    var listenerCounter = 0;

    var ListenerRegistry = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.propListeners = {
            children: {},
            listeners: {},
            ref: this.ankorSystem.getRef("")
        };
        this.treeListeners = {
            children: {},
            listeners: {},
            ref: this.ankorSystem.getRef("")
        };
    };
    ListenerRegistry.prototype.addListener = function(type, ref, cb) {
        var listeners = null;
        if (type == "propChange") {
            listeners = this._resolveListenersForRef(this.propListeners, ref);
        }
        else if (type == "treeChange") {
            listeners = this._resolveListenersForRef(this.treeListeners, ref);
        }

        var listenerId = "#" + listenerCounter++;
        listeners.listeners[listenerId] = cb;
        return {
            remove: this.ankorSystem.utils.hitch(this, function() {
                delete listeners.listeners[listenerId];
            })
        };
    };
    ListenerRegistry.prototype.triggerListeners = function(ref, message) {
        //Trigger treeChangeListeners
        var listeners = this._resolveListenersForRef(this.treeListeners, ref);
        while (listeners) {
            //Trigger listeners on this level
            console.log("Firing TREE ", listeners.ref.path());
            for (var id in listeners.listeners) {
                var listener = listeners.listeners[id];
                listener(ref, message);
            }

            //Go up one level
            var currentRef = listeners.ref;
            listeners = null;
            if (currentRef.segments.length > 0) {
                listeners = this._resolveListenersForRef(this.treeListeners, currentRef.parent())
            }
        }

        //Trigger propChangeListeners
        var listenersToTrigger = [
            this._resolveListenersForRef(this.propListeners, ref)
        ];
        while (listenersToTrigger.length > 0) {
            var listeners = listenersToTrigger.shift();
            if (!listeners.ref.isValid()) continue;

            //Trigger listeners on this level
            console.log("Firing PROP ", listeners.ref.path());
            for (var id in listeners.listeners) {
                var listener = listeners.listeners[id];
                listener(listeners.ref, message);
            }

            //Add child listeners to the listenersToTriggerList
            for (var childName in listeners.children) {
                listenersToTrigger.push(listeners.children[childName]);
            }
        }
    };
    ListenerRegistry.prototype.removeInvalidListeners = function(ref) {
        //Removes all listeners that are descendants of the given ref that are no longer valid (for a ref that points nowhere)
        var filterObsoleteListeners = function(listeners) {
            //Check every child if it's still valid
            var segmentStrings = [];
            for (var segmentString in listeners.children) {
                segmentStrings.push(segmentString);
            }
            for (var i = 0, segmentString; (segmentString = segmentStrings[i]); i++) {
                var childListeners = listeners.children[segmentString];
                if (childListeners.ref.isValid()) {
                    filterObsoleteListeners(childListeners);
                }
                else {
                    console.log("Invalidating Listener", childListeners.ref.path());
                    delete listeners.children[segmentString];
                }
            }
        };

        filterObsoleteListeners(this._resolveListenersForRef(this.propListeners, ref));
        filterObsoleteListeners(this._resolveListenersForRef(this.treeListeners, ref));
    };
    ListenerRegistry.prototype._resolveListenersForRef = function(listeners, ref) {
        var resolvedListeners = listeners;
        var currentRef = listeners.ref;
        for (var i = 0, segment; (segment = ref.segments[i]); i++) {
            var segmentString = null;
            if (segment.property != undefined) {
                segmentString = segment.property;
                currentRef = currentRef.append(segment.property);
            }
            else if (segment.index != undefined) {
                segmentString = "[" + segment.index + "]";
                currentRef = currentRef.appendIndex(segment.index);
            }

            if (!(segmentString in resolvedListeners.children)) {
                resolvedListeners.children[segmentString] = {
                    children: {},
                    listeners: {},
                    ref: currentRef
                }
            }
            resolvedListeners = resolvedListeners.children[segmentString];
        }
        return resolvedListeners;
    };

    return ListenerRegistry;
});
