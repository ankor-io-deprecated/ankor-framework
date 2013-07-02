define([
    "dojo/_base/declare",
    "dojo/json",
    "../model/ModelRef",
    "./ActionMessage",
    "./ChangeMessage"
], function(declare, json, ModelRef, ActionMessage, ChangeMessage) {
    return declare(null, {
        encodeMessages: function(messages) {
            var encodedMessages = [];
            for (var i = 0, message; (message = messages[i]); i++) {
                var encodedMessage = {
                    id: message.id,
                    type: message.type
                }
                if (message.type == "action") {
                    encodedMessage.data = {
                        name: message.name
                    }
                }
                else {
                    throw new Error("Can't encode unsupported message type " + message.type);
                }
                encodedMessages.push(encodedMessage);
            }
            return json.stringify(encodedMessages);
        },
        decodeMessages: function(messages) {
            var decodedMessages = [];
            if (typeof messages == "string") {
                messages = json.parse(messages);
            }
            for (var i = 0, message; (message = messages[i]); i++) {
                if (message.type == "change") {
                    decodedMessage = new ChangeMessage(message.id, new ModelRef(message.data.path), message.data.value);
                    decodedMessages.push(decodedMessage);
                }
                else {
                    throw new Error("Can't decode unsupported message type " + message.type);
                }
            }
            return decodedMessages;
        }
    });
});
