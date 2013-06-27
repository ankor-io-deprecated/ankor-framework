assert = require("assert")
{Context} = require("../model/Context")

exports.MemoryStore = class MemoryStore
    constructor: ->
        @contexts = {}

    save: (context, cb) ->
        assert(context instanceof Context, "MemoryStore#save only accepts Context objects")
        
        @contexts[context.id] = context
        
        if cb
            cb(null)

    load: (contextId, cb) ->
        if not cb 
            return

        if contextId not of @contexts
            cb(new Error("Context not found"))
        else
            cb(null, @contexts[contextId])
