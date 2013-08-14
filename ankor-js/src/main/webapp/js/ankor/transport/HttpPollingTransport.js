define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
    "../utils"
], function(BaseTransport, ActionMessage, ChangeMessage, utils) {
    var HttpPollingTransport = function(endpoint) {
        BaseTransport.call(this);

        this.endpoint = endpoint;
        this.inFlight = null;
        this.sendTimer = setTimeout(utils.hitch(this, "processOutgoingMessages"), 1000);
    };
    HttpPollingTransport.prototype = new BaseTransport();

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

        //Add fake message if there's no outgoing message
        if (this.inFlight.length == 0) {
            var messageId = this.ankorSystem.senderId + "#" + this.messageCounter++;
            var message = new ActionMessage(this.ankorSystem.senderId, this.ankorSystem.modelId, messageId, "", "poll");
            this.inFlight.push(message);
        }
        else {
            for (var i = 0, message; (message = this.inFlight[i]); i++) {
                console.log("OUT", message);
            }
        }

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
        utils.xhrPost(this.endpoint, {
            messages: utils.jsonStringify(jsonMessages)
        }, utils.hitch(this, function(err, messages) {
            if (err) {
                this.outgoingMessages = this.inFlight.concat(this.outgoingMessages);
            }
            else {
                for (var i = 0, message; (message = messages[i]); i++) {
                    if (message.change != undefined) {
                        var messageObject = new ChangeMessage(message.senderId, message.modelId, message.messageId, message.property, message.change.newValue);
                        console.log("IN", messageObject);
                        this.processIncomingMessage(messageObject);
                    }
                }
            }
            this.inFlight = null;
            this.sendTimer = setTimeout(utils.hitch(this, "processOutgoingMessages"), 1000);
        }));
    };

    return HttpPollingTransport;
});
