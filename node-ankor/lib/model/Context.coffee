uuid = require("node-uuid")
assert = require("assert")
{bind} = require("underscore")
{ModelType} = require("./Type")
{ModelObject} = require("./ModelObject")
{ModelRef} = require("./ModelRef")
{MessageQueue} = require("../message/MessageQueue")

exports.Context = class Context
    constructor: (ankorSystem, model) ->
        @ankorSystem = ankorSystem                  #Reference to AnkorSystem
        @id = uuid.v4()                             #Id of this context/model instance
        @model = model                              #Reference to model

        @incomingMessageQueue = new MessageQueue()  #Incoming message queue
        @outgoingMessageQueue = new MessageQueue()  #Outgoing message queue

        @incomingMessageQueue.registerNewMessageListener(bind(@processIncomingMessages, @))

    createModelObject: (name) ->
        type = @ankorSystem.typeRegistry.getType(name)
        assert(type instanceof ModelType, "createModelObject can only create named ModelTypes (not enum, ...)")
        return new ModelObject(type)

    createModelRef: (path) ->
        return new ModelRef(@, path)

    processIncomingMessages: (messageQueue) ->
        messages = messageQueue.dequeue()
        if messages.length == 0
            return

        for message in messages
            if message.type == "action"
                @ankorSystem.listenerRegistry.triggerActionEvent(@, message.name)
            else
                throw new Error("Can't process unsupported message type #{message.type}")
