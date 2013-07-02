define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojox/uuid/generateRandomUuid",
    "./ListenerRegistry",
    "./model/ModelRef",
    "./model/ModelObject",
    "./message/ActionMessage"
], function(declare, lang, generateUuid, ListenerRegistry, ModelRef, ModelObject, ActionMessage) {
    return declare(null, {
        constructor: function(config) {
            this.contextId = null;
            this.transport = null;
            this.model = new ModelObject(this, new ModelRef("/"));
            this.listenerRegistry = new ListenerRegistry();

            lang.mixin(this, config);
            if (!this.transport) throw new Error("AnkorSystem has no transport");
            if (!this.contextId) throw new Error("AnkorSystem has no contextId");

            this.transport.connect(this);
        },
        processMessages: function(messages) {
            for (var i = 0, message; (message = messages[i]); i++) {
                if (message.type == "change") {
                    var modelObject = message.ref.getBaseObject(this.model);
                    var propertyName = message.ref.getPropertyName();
                    modelObject.apply(propertyName, message.value);
                }
                else {
                    throw new Error("Unsupported message type " + message.type);
                }
            }
        },
        action: function(actionName) {
            var action = new ActionMessage(generateUuid(), actionName)
            this.transport.sendMessage(action);
        },
        onChange: function(ref, listener) {
            if (!(ref instanceof ModelRef)) {
                ref = new ModelRef(ref);
            }
            return this.listenerRegistry.registerChangeListener(ref, listener);
        },
        onAction: function(name, listener) {
            return this.listenerRegistry.registerActionListener(name, listener);
        }
    });
});
