define([
    "./ListenerRegistry",
    "./Model",
    "./Path",
    "./Ref",
], function(ListenerRegistry, Model, Path, Ref) {
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

    AnkorSystem.prototype.getRef = function(pathOrString) {
        if (!(pathOrString instanceof Path)) {
            pathOrString = new Path(pathOrString);
        }
        return new Ref(this, pathOrString);
    };

    AnkorSystem.prototype.addListener = function(type, path, cb) {
        return this.listenerRegistry.addListener(type, path, cb);
    };

    AnkorSystem.prototype.triggerListeners = function(path, event) {
        this.listenerRegistry.triggerListeners(path, event);
    };

    AnkorSystem.prototype.removeInvalidListeners = function(path) {
        this.listenerRegistry.removeInvalidListeners(path);
    };

    AnkorSystem.prototype.onIncomingEvent = function(event) {
        var ref = this.getRef(event.path);
        ref._handleEvent(event);
    };

    return AnkorSystem;
});
