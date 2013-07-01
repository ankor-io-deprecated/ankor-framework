define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojox/uuid/generateRandomUuid",
    "./message/ActionMessage"
], function(declare, lang, generateUuid, ActionMessage) {
    return declare(null, {
        constructor: function(config) {
            this.contextId = null;
            this.transport = null;
            this.model = {}; //Todo: I probably don't want a simple Stateful, because I need something like getRef()

            lang.mixin(this, config);
            if (!this.transport) throw new Error("AnkorSystem has no transport");
            if (!this.contextId) throw new Error("AnkorSystem has no contextId");

            this.transport.connect(this.contextId);
        },
        action: function(actionName) {
            action = new ActionMessage(generateUuid(), actionName)
            this.transport.sendMessage(action);
        },
        onChange: function(ref, listener) {
            //a
        },
        onAction: function(listener) {
            //a
        }
    });
});
