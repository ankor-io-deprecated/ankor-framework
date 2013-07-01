async = require("async")

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

    registerChangeListener: (path, listener) ->
        listenerId = @listenerId++
        if path not of @changeListeners
            @changeListeners[path] = {}
        @changeListeners[path][listenerId] = listener
        return {
            path: path,
            listenerId: listenerId,
            remove: =>
                delete @changeListeners[path][listenerId]
        }

    unregisterActionListener: (path, listenerId) ->
        delete @changeListeners[path][listenerId]

    triggerChangeEvent: (context, path, oldValue, newValue, cb) ->
        listeners = []
        for listenerPath, registeredListeners of @changeListeners
            if path.indexOf(listenerPath) == 0
                for listenerId, listener of registeredListeners
                    listeners.push({
                        path: listenerPath,
                        fn: listener
                    })
        if listeners.length == 0
            if cb
                cb()
            return

        qErr = null
        q = async.queue((listener, cb) =>
            subPath = listener.path.substr(path.length)
            subPath = subPath.split("/")
            subPath = (subLevel for subLevel in subPath when subLevel != "")
            #Todo: subpath event resolution...
            console.log("SubPath:", subPath)
            listener.fn(listener.path, context, oldValue, newValue, cb)
        , 1)
        q.drain = ->
            if cb
                cb(qErr)
        q.push(listeners, (err) ->
            if err
                qErr = err
        )
