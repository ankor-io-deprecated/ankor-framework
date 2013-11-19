define([
    "./ListenerRegistry",
    "./Model",
    "./Ref"
], function(ListenerRegistry, Model, Ref) {
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
        this.model = new Model(this.getRef(""));
        this.listenerRegistry = new ListenerRegistry(this);

        this.transport.init(this);
    };

    AnkorSystem.prototype.getRef = function(path) {
        return new Ref(this, path);
    };

    AnkorSystem.prototype.addListener = function(type, ref, cb) {
        return this.listenerRegistry.addListener(type, ref, cb);
    };

    AnkorSystem.prototype.triggerListeners = function(ref, message) {
        this.listenerRegistry.triggerListeners(ref, message);
    };

    AnkorSystem.prototype.removeInvalidListeners = function(ref) {
        this.listenerRegistry.removeInvalidListeners(ref);
    };

    AnkorSystem.prototype.processIncomingMessage = function(message) {
        var ref = this.getRef(message.property);

        if (message.type == message.TYPES["VALUE"]) {
            ref.setValue(message.value, true);
        }
        else if (message.type == message.TYPES["DEL"]) {
            ref.append(message.key.toString()).del(true);
        }
        else if (message.type == message.TYPES["INSERT"]) {
            ref.insert(message.key.toString(), message.value, true);
        }
        else if (message.type == message.TYPES["REPLACE"]) {
            for(var i = 0; i < message.value.length; i++) {
                ref.appendIndex(message.key + i).setValue(message.value[i], true);
            }
        }
    };

    return AnkorSystem;
});
