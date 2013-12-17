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

    Ref.prototype.setValue = function(value, source) {
        //Apply value to model
        this.ankorSystem.model.setValue(this.path, value);

        //Cleanup listeners
        if (value === null) {
            this.ankorSystem.removeInvalidListeners(this.path.parent());
        }

        //Build event
        var event = new ChangeEvent(this.path, source, ChangeEvent.TYPE.VALUE, null, value);

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
    };

    //Removes this ref from the parent (regardless of map or array)
    Ref.prototype.del = function(source) {
        //Apply change to model
        this.ankorSystem.model.del(this.path);

        //Cleanup listeners
        var parentPath = this.path.parent();
        this.ankorSystem.removeInvalidListeners(parentPath);

        //Build event
        var event = new ChangeEvent(parentPath, source, ChangeEvent.TYPE.DEL, this.propertyName(), null);

        //Trigger listeners
        this.ankorSystem.triggerListeners(parentPath, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
    };

    Ref.prototype.insert = function(index, value, source) {
        this.ankorSystem.model.insert(this.path, index, value);

        //Build event
        var event = new ChangeEvent(this.path, source, ChangeEvent.TYPE.INSERT, index, value);

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
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

    Ref.prototype._handleEvent = function(event) {
        if (event instanceof ChangeEvent) {
            if (event.type === ChangeEvent.TYPE.VALUE) {
                //Apply value to model
                this.ankorSystem.model.setValue(this.path, event.value);

                //Cleanup listeners
                if (value === null) {
                    this.ankorSystem.removeInvalidListeners(this.path.parent());
                }

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.DEL) {
                //Apply change to model
                this.ankorSystem.model.del(this.path.append(event.key.toString()));

                //Cleanup listeners
                this.ankorSystem.removeInvalidListeners(this.path);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.INSERT) {
                //Apply change to model
                this.ankorSystem.model.insert(this.path, event.key.toString(), event.value);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.REPLACE) {
                //Apply change to model
                for(var i = 0; i < event.value.length; i++) {
                    var path = this.path.appendIndex(event.key + i);
                    var value = event.value[i];
                    this.ankorSystem.model.setValue(path, value);
                }

                //Cleanup listeners
                this.ankorSystem.removeInvalidListeners(this.path);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
        }
    };

    Ref.prototype.fire = function(actionName, params) {
        var event = new ActionEvent(this.path, null, actionName, params);
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
