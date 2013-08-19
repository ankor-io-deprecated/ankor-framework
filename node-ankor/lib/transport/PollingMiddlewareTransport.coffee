###
# Connect Middleware
# 
# Requires: bodyParser Middleware
#
# Config:
# {
#   path: "/ankor"
#   contextResolver: function(req, cb) {
#       cb(err, contextId)
#   }
# }
###

assert = require("assert")
async = require("async")
{JsonMapper} = require("../message/JsonMapper")

exports.PollingMiddlewareTransport = class PollingMiddlewareTransport
    constructor: (config) ->
        @ankorSystem = null
        @mapper = new JsonMapper()

        @path = config?.path
        @path ?= "/ankor"

        @contextResolver = config?.contextResolver
        @contextResolver ?= (req, cb) ->
            if req.session?.ankorContextId
                cb(null, req.session.ankorContextId)
            else
                cb(new Error("No ankorContextId in session"))

    middleware: ->
        return (req, res, next) =>
            if req.method != "POST" or req.url != @path
                return next()

            @contextResolver(req, (err, contextId) =>
                try
                    if err
                        throw err

                    #Get context
                    context = @ankorSystem.contextRegistry.getContext(contextId)

                    #Handle incoming messages
                    incomingMessages = @mapper.decodeMessages(req.body.messages)
                    context.incomingMessageQueue.queue(incomingMessages)

                    #Prepare outgoing messages
                    outgoingMessages = context.outgoingMessageQueue.dequeue()
                    encodedOutgoingMessages = @mapper.encodeMessages(outgoingMessages)

                catch catchedError
                    err = catchedError

                    if outgoingMessages
                        context.outgoingMessageQueue.requeue(outgoingMessages)

                if err
                    res.statusCode = 500
                    res.end(JSON.stringify({
                        contextId: contextId,
                        error: true,
                        msg: err.toString()
                    }))
                else
                    res.end(JSON.stringify({
                        contextId: contextId,
                        messages: encodedOutgoingMessages
                    }))
            )
