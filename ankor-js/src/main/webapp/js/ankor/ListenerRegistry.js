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
    ListenerRegistry.prototype.addListener = function(type, path, cb) {
        var listeners = null;
        if (type == "propChange") {
            listeners = this._resolveListenersForPath(this.propListeners, path);
        }
        else if (type == "treeChange") {
            listeners = this._resolveListenersForPath(this.treeListeners, path);
        }

        var listenerId = "#" + listenerCounter++;
        listeners.listeners[listenerId] = cb;
        return {
            remove: function() {
                delete listeners.listeners[listenerId];
                //Todo: Maybe clean up listener trees that no longer have any listeners...
            }
        };
    };
    ListenerRegistry.prototype.triggerListeners = function(path, event) {
        //Trigger treeChangeListeners
        var id, listeners, listener;

        listeners = this._resolveListenersForPath(this.treeListeners, path);
        while (listeners) {
            //Trigger listeners on this level
            //console.log("Firing TREE ", listeners.ref.path());
            for (id in listeners.listeners) {
                if (!listeners.listeners.hasOwnProperty(id)) {
                    continue;
                }
                listener = listeners.listeners[id];
                listener(listeners.ref, event);
            }

            //Go up one level
            var currentPath = listeners.ref.path;
            listeners = null;
            if (currentPath.segments.length > 0) {
                listeners = this._resolveListenersForPath(this.treeListeners, currentPath.parent())
            }
        }

        //Trigger propChangeListeners
        var listenersToTrigger = [
            this._resolveListenersForPath(this.propListeners, path)
        ];
        var pathsToCleanup = {};
        var first = true;
        while (listenersToTrigger.length > 0) {
            listeners = listenersToTrigger.shift();
            if (!listeners.ref.isValid()) {
                var refToCleanup = listeners.ref.parent();
                while (!refToCleanup.isValid()) {
                    refToCleanup = refToCleanup.parent();
                }
                pathsToCleanup[refToCleanup.path.toString()] = refToCleanup.path;
                continue;
            }

            //Trigger listeners on this level
            //console.log("Firing PROP ", listeners.ref.path());
            for (id in listeners.listeners) {
                if (!listeners.listeners.hasOwnProperty(id)) {
                    continue;
                }
                listener = listeners.listeners[id];
                listener(listeners.ref, event);
            }

            //Add child listeners to the listenersToTriggerList
            if (!first || event.type == event.TYPE.VALUE) {
                //Propagate to all children if it's a VALUE change event or if it's not the first level
                for (var childName in listeners.children) {
                    if (!listeners.children.hasOwnProperty(childName)) {
                        continue;
                    }
                    listenersToTrigger.push(listeners.children[childName]);
                }
            }
            else {
                //Otherwise (if first) only notify affected children for INSERT and REPLACE, and nobody for DEL (invalid ref anyways)
                var key;
                if (event.type == event.TYPE.INSERT) {
                    key = event.key.toString();
                    if (key in listeners.children) {
                        listenersToTrigger.push(listeners.children[key]);
                    }
                }
                else if (event.type == event.TYPE.REPLACE) {
                    var index = event.key;
                    for (var i = 0; i < event.value.length; i++) {
                        key = (index + i).toString();
                        if (key in listeners.children) {
                            listenersToTrigger.push(listeners.children[key]);
                        }
                    }
                }
            }

            first = false;
        }

        //Cleanup found invalid listeners
        for (var pathString in pathsToCleanup) {
            if (!pathsToCleanup.hasOwnProperty(pathString)) {
                continue;
            }
            this.removeInvalidListeners(pathsToCleanup[pathString]);
        }
    };
    ListenerRegistry.prototype.removeInvalidListeners = function(path) {
        //Removes all listeners that are descendants of the given path that are no longer valid (for a ref that points nowhere)
        var filterObsoleteListeners = function(listeners) {
            //Check every child if it's still valid
            var segmentString,
                segmentStrings = [];
            for (segmentString in listeners.children) {
                if (!listeners.children.hasOwnProperty(segmentString)) {
                    continue;
                }
                segmentStrings.push(segmentString);
            }
            for (var i = 0; (segmentString = segmentStrings[i]); i++) {
                var childListeners = listeners.children[segmentString];
                if (childListeners.ref.isValid()) {
                    filterObsoleteListeners(childListeners);
                }
                else {
                    //console.log("Invalidating Listener", childListeners.ref.path());
                    delete listeners.children[segmentString];
                }
            }
        };

        filterObsoleteListeners(this._resolveListenersForPath(this.propListeners, path));
        filterObsoleteListeners(this._resolveListenersForPath(this.treeListeners, path));
    };
    ListenerRegistry.prototype._resolveListenersForPath = function(listeners, path) {
        var resolvedListeners = listeners;
        var currentRef = listeners.ref;
        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            if (segment.isProperty()) {
                currentRef = currentRef.append(segment.key);
            }
            else if (segment.isIndex()) {
                currentRef = currentRef.appendIndex(segment.key);
            }

            var key = segment.key.toString();
            if (!(key in resolvedListeners.children)) {
                resolvedListeners.children[key] = {
                    children: {},
                    listeners: {},
                    ref: currentRef
                }
            }
            resolvedListeners = resolvedListeners.children[key];
        }
        return resolvedListeners;
    };

    return ListenerRegistry;
});
