define([
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
], function(ActionMessage, ChangeMessage) {
    var BaseTransport = function() {
        this.outgoingMessages = [];
        this.messageCounter = 0;
    };

    BaseTransport.prototype.init = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.utils = ankorSystem.utils;
    };

    BaseTransport.prototype.sendMessage = function(message) {
        message.messageId = message.senderId + "#" + this.messageCounter++;
        this.outgoingMessages.push(message);
        if (this.ankorSystem.debug) {
            console.log("OUT", message);
        }
    };

    BaseTransport.prototype.receiveIncomingMessage = function(serializedMessage) {
        try {
            var message = this.utils.jsonParse(serializedMessage);
        } catch (e) {
            if (this.ankorSystem.debug) {
                console.log("Ankor Transport Error", e);
            }
            return;
        }

        if (message.change != undefined) {
            var messageObject = new ChangeMessage(message.senderId, message.modelId, message.messageId, message.property, message.change.type, message.change.key, message.change.value);
            this.processIncomingMessage(messageObject);
        }
    }

    BaseTransport.prototype.processIncomingMessage = function(message) {
        if (this.ankorSystem.debug) {
            console.log("IN", message);
        }
        this.ankorSystem.processIncomingMessage(message);
    };

    BaseTransport.buildJsonMessages = function(messages) {
        var jsonMessages = [];
        for (var i = 0, message; (message = messages[i]); i++) {
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
                    type: message.type,
                    key: message.key,
                    value: message.value
                };
            }
            jsonMessages.push(jsonMessage);
        }
        return jsonMessages;
    };

    return BaseTransport;
});
