define([
    "dojo/_base/declare",
    "dojo/_base/lang",
], function(declare, lang) {
    return declare(null, {
        transport: null,
        contextId: null,

        constructor: function(config) {
            lang.mixin(this, config);

            this.model = {}; //Todo...

            if (!this.transport) throw new Error("AnkorSystem has no transport");
            if (!this.contextId) throw new Error("AnkorSystem has no contextId");

            this.transport.connect(this.contextId);
        }
    });
});
