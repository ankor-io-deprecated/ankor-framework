{isArray} = require("underscore")

exports.MessageQueue = class MessageQueue
    constructor: ->
        @_messages = []
        @_listeners = []

    queue: (messages) ->
        if not isArray(messages)
            messages = [messages]
        @_messages = @_messages.concat(messages)
        @notifyNewMessageListeners()
        
    dequeue: ->
        messages = @_messages
        @_messages = []
        return messages

    requeue: (messages) ->
        if not isArray(messages)
            messages = [messages]
        @_messages = messages.concat(@_messages)
        @notifyNewMessageListeners()

    registerNewMessageListener: (listener) ->
        @_listeners.push(listener)

    notifyNewMessageListeners: ->
        for listener in @_listeners
            listener(@)
            