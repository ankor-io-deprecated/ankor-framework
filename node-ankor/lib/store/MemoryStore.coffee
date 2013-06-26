assert = require("assert")
{Context} = require("../model/Context")

exports.MemoryStore = class MemoryStore
    constructor: ->
        @contexts = {}

    save: (context, cb) ->
        assert(context instanceof Context, "MemoryStore#save only accepts Context objects")
        
        @contexts[context.uuid] = context
        
        if cb
            cb(null)

    load: (contextUuid, cb) ->
        if not cb 
            return

        if contextUuid not of @contexts
            cb(new Error("Context not found"))
        else
            cb(null, @contexts[contextUuid])
