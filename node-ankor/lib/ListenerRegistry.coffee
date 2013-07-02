async = require("async")
{ModelRef} = require("./model/ModelRef")

exports.ListenerRegistry = class ListenerRegistry
    constructor: ->
        @listenerId = 0
        @actionListeners = {}
        @changeListeners = {}

    registerActionListener: (actionName, listener) ->
        listenerId = @listenerId++
        if actionName not of @actionListeners
            @actionListeners[actionName] = {}
        @actionListeners[actionName][listenerId] = listener
        return {
            actionName: actionName,
            listenerId: listenerId,
            remove: =>
                @unregisterActionListener(actionName, listenerId)
        }

    unregisterActionListener: (actionName, listenerId) ->
        delete @actionListeners[actionName][listenerId]

    triggerActionEvent: (context, actionName, cb) ->
        listeners = []
        if @actionListeners[actionName]
            for listenerId, listener of @actionListeners[actionName]
                listeners.push(listener)
        if listeners.length == 0
            if cb
                cb()
            return

        qErr = null
        q = async.queue((listener, cb) =>
            listener(actionName, context, cb)
        , 1)
        q.drain = ->
            if cb
                cb(qErr)
        q.push(listeners, (err) ->
            if err
                qErr = err
        )

    registerChangeListener: (ref, listener) ->
        listenerId = @listenerId++
        path = ref.getPath()
        if path not of @changeListeners
            @changeListeners[path] = {}
        @changeListeners[path][listenerId] = listener
        return {
            ref: ref,
            listenerId: listenerId,
            remove: =>
                delete @changeListeners[path][listenerId]
        }

    triggerChangeEvent: (context, ref, oldValue, newValue, cb) ->
        listeners = []
        for listenerPath, registeredListeners of @changeListeners
            listenerRef = new ModelRef(listenerPath)
            if listenerRef.matches(ref)
                for listenerId, listener of registeredListeners
                    listeners.push(listener)
        if listeners.length == 0
            if cb
                cb()
            return

        qErr = null
        q = async.queue((listener, cb) =>
            listener(ref, context, oldValue, newValue, cb)
        , 1)
        q.drain = ->
            if cb
                cb(qErr)
        q.push(listeners, (err) ->
            if err
                qErr = err
        )
