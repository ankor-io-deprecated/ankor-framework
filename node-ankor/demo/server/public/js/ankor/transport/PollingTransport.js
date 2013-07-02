define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/request",
    "../message/JsonMapper"
], function(declare, lang, request, JsonMapper) {
    var POLLINGINTERVAL = 1000;

    return declare(null, {
        //PUBLIC API
        constructor: function(config) {
            this.ankorSystem = null;
            this.mapper = new JsonMapper();
            
            this.url = "/ankor";
            if (config && config.url) {
                this.url = config.url;
            }

            this._outgoingMessages = [];
            this._inFlight = false;
            this._requestTimer = null;
        },
        connect: function(ankorSystem) {
            this.ankorSystem = ankorSystem;
            this._doRequest();
        },
        sendMessage: function(message) {
            this._outgoingMessages.push(message);
            this._scheduleRequest();
        },

        //INTERNAL API
        _scheduleRequest: function() {
            var timeout = POLLINGINTERVAL;
            if (this._outgoingMessages.length > 0) {
                timeout = 0;
            }
            if (!this._requestTimer && !this._inFlight) {
                this._requestTimer = setTimeout(lang.hitch(this, "_doRequest"), timeout);
            }
        },
        _clearScheduledRequest: function() {
            if (this._requestTimer) {
                clearTimeout(this._requestTimer);
                this._requestTimer = null;
            }
        },
        _doRequest: function() {
            this._clearScheduledRequest();
            var messages = this._outgoingMessages;
            this._outgoingMessages = [];
            this._inFlight = true;

            request.post(this.url, {
                handleAs: "json",
                data: {
                    contextId: this.ankorSystem.contextId,
                    messages: this.mapper.encodeMessages(messages)
                }
            }).then(lang.hitch(this, function(data) {
                this._inFlight = false;
                this._scheduleRequest();

                messages = this.mapper.decodeMessages(data.messages);
                this.ankorSystem.processMessages(messages);
            }), lang.hitch(this, function(error) {
                this._inFlight = false;
                this._scheduleRequest();

                //Todo: Error Handling (e.g. reschedule failed messages)
                console.log("Error", error);
            }));
        }
    });
});
