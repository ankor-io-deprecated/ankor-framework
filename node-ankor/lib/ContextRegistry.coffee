assert = require("assert")
{Context} = require("./model/Context")

exports.ContextRegistry = class ContextRegistry
    constructor: ->
        @_contexts = {}

    registerContext: (context) ->
        assert(context instanceof Context, "ContextRegistry expects context objects only")        
        @_contexts[context.id] = context

    lookupContext: (contextId) ->
        if contextId not of @_contexts
            throw new Error("Context not found")
        else
            return @_contexts[contextId]
