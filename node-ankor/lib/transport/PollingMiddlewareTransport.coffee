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

            contextId = null
            context = null
            messages = null
            responseMessages = null

            async.series([
                #Parse messages
                (cb) =>
                    err = null
                    try
                        messages = @mapper.decodeMessages(req.body.messages)
                    catch mappingError
                        err = mappingError
                    cb(err)

                #Resolve context id
                (cb) =>
                    @contextResolver(req, (err, resolvedContextId) =>
                        if not err
                            contextId = resolvedContextId
                        cb(err)
                    )

                #Load context
                (cb) =>
                    @ankorSystem.store.load(contextId, (err, loadedContext) ->
                        if not err
                            context = loadedContext
                        cb(err)
                    )

                #Process messages
                (cb) =>
                    context.incomingMessages = context.incomingMessages.concat(messages)
                    context.processIncomingMessages(cb)

                #Encode response and save context
                (cb) =>
                    responseMessages = @mapper.encodeMessages(context.outgoingMessages)
                    context.outgoingMessages = []
                    context.save(cb)

            ], (err) =>
                if err
                    res.end(JSON.stringify({
                        contextId: contextId,
                        error: true,
                        msg: err.toString()
                    }))
                else
                    res.end(JSON.stringify({
                        contextId: contextId,
                        messages: responseMessages
                    }))
            )
