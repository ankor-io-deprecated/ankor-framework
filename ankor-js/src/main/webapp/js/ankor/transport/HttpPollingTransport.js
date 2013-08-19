define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
], function(BaseTransport, ActionMessage, ChangeMessage) {
    var HttpPollingTransport = function(endpoint, options) {
        BaseTransport.call(this);

        this.endpoint = endpoint;
        this.inFlight = null;
        this.pollingInterval = 100;
        if (options && options.pollingInterval != undefined) {
            this.pollingInterval = options.pollingInterval;
        }
    };
    HttpPollingTransport.prototype = new BaseTransport();

    HttpPollingTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);
        this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
    };

    HttpPollingTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        if (!this.inFlight) {
            this.processOutgoingMessages();
        }
    };

    HttpPollingTransport.prototype.processOutgoingMessages = function() {
        clearTimeout(this.sendTimer);
        this.inFlight = this.outgoingMessages;
        this.outgoingMessages = [];

        //Build JSON of messages
        var jsonMessages = [];
        for (var i = 0, message; (message = this.inFlight[i]); i++) {
            var jsonMessage = {
                senderId: message.senderId,
                modelId: message.modelId,
                messageId: message.messageId,
                property: message.property
            };
            if (message instanceof ActionMessage) {
                jsonMessage.action = message.action;
            }
            else if (message instanceof ChangeMessage) {
                jsonMessage.change = {
                    newValue: message.value
                };
            }
            jsonMessages.push(jsonMessage);
        }

        //Ajax request
        this.utils.xhrPost(this.endpoint, {
            clientId: this.ankorSystem.senderId,
            messages: this.utils.jsonStringify(jsonMessages)
        }, this.utils.hitch(this, function(err, response) {
            try {
                var messages = this.utils.jsonParse(response);
            }
            catch (e) {
                err = e;
            }
            if (err) {
                if (this.ankorSystem.debug) {
                    console.log("Ankor HttpPollingTransport Error", err);
                }
                this.outgoingMessages = this.inFlight.concat(this.outgoingMessages);
            }
            else {
                for (var i = 0, message; (message = messages[i]); i++) {
                    if (message.change != undefined) {
                        var messageObject = new ChangeMessage(message.senderId, message.modelId, message.messageId, message.property, message.change.newValue);
                        this.processIncomingMessage(messageObject);
                    }
                }
            }
            this.inFlight = null;
            this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
        }));
    };

    return HttpPollingTransport;
});
