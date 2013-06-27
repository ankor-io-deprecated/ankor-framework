uuid = require("node-uuid")

exports.Context = class Context
    constructor: (ankorSystem, rootModel) ->
        @ankorSystem = ankorSystem
        @model = rootModel
        @session = {}
        @id = uuid.v4()

    save: (cb) ->
        @ankorSystem.store.save(@, cb)
