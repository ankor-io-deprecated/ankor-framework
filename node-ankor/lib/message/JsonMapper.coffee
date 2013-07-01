{ActionMessage} = require("./ActionMessage")
{ChangeMessage} = require("./ChangeMessage")

exports.JsonMapper = class JsonMapper
    encodeMessages: (messages, stringify) ->
        encodedMessages = []
        for message in messages
            encodedMessage = {
                id: message.id,
                type: message.type
            }
            if message.type == "change"
                encodedMessage.data = {
                    path: message.path,
                    value: message.value
                }
            else
                throw new Error("Can't encode unsupported message type #{message.type}")
            encodedMessages.push(encodedMessage)
        
        if stringify
            encodedMessages = JSON.stringify(encodedMessages)
        return encodedMessages

    decodeMessages: (messages) ->
        decodedMessages = []
        messages = JSON.parse(messages)
        for message in messages
            if message.type == "action"
                decodedMessage = new ActionMessage(message.id, message.data.name)
                decodedMessages.push(decodedMessage)
            else
                throw new Error("Can't decode unsupported message type #{message.type}")
        return decodedMessages
