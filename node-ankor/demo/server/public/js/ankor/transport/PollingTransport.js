define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/json",
    "dojo/request"
], function(declare, lang, json, request) {
    var POLLINGINTERVAL = 1000;

    return declare(null, {
        //PUBLIC API
        constructor: function(url) {
            this.url = url;
        },
        connect: function(contextId) {
            this.contextId = contextId;
            
            this._messages = [];
            this._inFlight = false;
            this._requestTimer = null;
            this._doRequest();
        },
        sendMessage: function(message) {
            this._messages.push(message);
            this._scheduleRequest();
        },

        //INTERNAL API
        _scheduleRequest: function() {
            var timeout = POLLINGINTERVAL;
            if (this._messages.length > 0) {
                timeout = 0;
            }
            if (!this._requestTimer) {
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
            var messages = this._messages;
            this._messages = [];
            this._inFlight = true;

            request.post(this.url, {
                handleAs: "json",
                data: {
                    contextId: this.contextId,
                    messages: json.stringify(messages)
                }
            }).then(lang.hitch(this, function(data) {
                this._inFlight = false;
                this._scheduleRequest();

                console.log("Success", data);
            }), lang.hitch(this, function(error) {
                this._inFlight = false;
                this._scheduleRequest();

                //Todo: Error Handling
                console.log("Error", error);
            }));
        }
    });
});
