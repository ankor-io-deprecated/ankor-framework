define([
    "./events/BaseEvent",
    "./events/ChangeEvent",
    "./events/ActionEvent"
], function(BaseEvent, ChangeEvent, ActionEvent) {
    var Ref = function(ankorSystem, path) {
        this.ankorSystem = ankorSystem;
        this.path = path;
    };

    //////////////////
    // PATH METHODS //
    //////////////////
    Ref.prototype.append = function(path) {
        return new Ref(this.ankorSystem, this.path.append(path));
    };

    Ref.prototype.appendIndex = function(index) {
        return new Ref(this.ankorSystem, this.path.appendIndex(index));
    };

    Ref.prototype.parent = function() {
        return new Ref(this.ankorSystem, this.path.parent())
    };

    Ref.prototype.propertyName = function() {
        return this.path.propertyName();
    };

    Ref.prototype.equals = function(ref) {
        return this.path.equals(ref.path);
    };

    ///////////////////
    // MODEL METHODS //
    ///////////////////

    Ref.prototype.getValue = function() {
        return this.ankorSystem.model.getValue(this.path);
    };

    Ref.prototype.setValue = function(value, eventOrEventSource) {
        //Apply value to model
        this.ankorSystem.model.setValue(this.path, value);

        //Cleanup listeners
        if (value === null) {
            this.ankorSystem.removeInvalidListeners(this.path.parent());
        }

        //Event logic
        var event = eventOrEventSource;
        var sendEvent = false;
        if (!(event instanceof BaseEvent)) {
            sendEvent = true;
            event = new ChangeEvent(this.path, eventOrEventSource, ChangeEvent.TYPE.VALUE, null, value);
        }

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        if (sendEvent) {
            this.ankorSystem.transport.sendEvent(event);
        }
    };

    //Removes this ref from the parent (regardless of map or array)
    Ref.prototype.del = function(eventOrEventSource) {
        //Apply change to model
        this.ankorSystem.model.del(this.path);

        //Cleanup listeners
        var parentPath = this.path.parent();
        this.ankorSystem.removeInvalidListeners(parentPath);

        //Event logic
        var event = eventOrEventSource;
        var sendEvent = false;
        if (!(event instanceof BaseEvent)) {
            sendEvent = true;
            event = new ChangeEvent(parentPath, eventOrEventSource, ChangeEvent.TYPE.DEL, this.propertyName(), null);
        }

        //Trigger listeners
        this.ankorSystem.triggerListeners(parentPath, event);

        //Send message
        if (sendEvent) {
            this.ankorSystem.transport.sendEvent(event);
        }
    };

    Ref.prototype.insert = function(index, value, eventOrEventSource) {
        this.ankorSystem.model.insert(this.path, index, value);

        //Event logic
        var event = eventOrEventSource;
        var sendEvent = false;
        if (!(event instanceof BaseEvent)) {
            sendEvent = true;
            event = new ChangeEvent(this.path, eventOrEventSource, ChangeEvent.TYPE.INSERT, index, value);
        }

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        if (sendEvent) {
            this.ankorSystem.transport.sendEvent(event);
        }
    };

    Ref.prototype.size = function() {
        return this.ankorSystem.model.size(this.path);
    };

    Ref.prototype.isValid = function() {
        return this.ankorSystem.model.isValid(this.path);
    };

    ///////////////////
    // EVENT METHODS //
    ///////////////////

    Ref.prototype.fire = function(actionName) {
        var event = new ActionEvent(this.path, null, actionName);
        this.ankorSystem.transport.sendEvent(event);
    };

    Ref.prototype.addPropChangeListener = function(cb) {
        return this.ankorSystem.addListener("propChange", this.path, cb);
    };

    Ref.prototype.addTreeChangeListener = function(cb) {
        return this.ankorSystem.addListener("treeChange", this.path, cb);
    };

    return Ref;
});
