define([
    "dojo/_base/declare",
    "dojo/json"
], function(declare, json) {
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
                    throw new Error("Can't encode unsupported message type " + message.type)
                }
                encodedMessages.push(encodedMessage);
            }
            return json.stringify(encodedMessages);
        }
    })
})
