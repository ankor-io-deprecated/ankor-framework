uuid = require("node-uuid")
async = require("async")

exports.Context = class Context
    constructor: (ankorSystem, model=null, attributes={}, incomingMessages=[], outgoingMessages=[]) ->
        @ankorSystem = ankorSystem  #Reference to AnkorSystem

        #Persisted
        @id = uuid.v4()                         #Id of this context/model instance
        @model = model                          #Reference to model
        @attributes = attributes                #Persistent attributes that are not part of the model
        @incomingMessages = incomingMessages    #Incoming message queue
        @outgoingMessages = outgoingMessages    #Outgoing message queue

    save: (cb) ->
        @ankorSystem.store.save(@, cb)

    processIncomingMessages: (cb) ->
        if @incomingMessages.length == 0
            cb()

        messages = @incomingMessages
        @incomingMessages = []

        qErr = null
        q = async.queue((message, cb) =>
            if message.type == "action"
                @ankorSystem.listenerRegistry.triggerActionEvent(@, message.name, cb)
            else
                cb(new Error("Can't process unsupported message type #{message.type}"))
        , 1)
        q.drain = ->
            cb(qErr)
        q.push(messages, (err) ->
            if err
                qErr = err
        )
