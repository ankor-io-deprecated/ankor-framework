{bind} = require("underscore")
{ContextDepending} = require("../base/ContextDepending")

exports.AppRouter = class AppRouter extends ContextDepending
    constructor: ->
        super()

        @dependOn("ankorService")

    init: (app) ->
        app.get("/", bind(@index, @))
        app.get("/debugMemoryStore", bind(@debugMemoryStore, @))

    index: (req, res, next) ->
        @ankorService.ankor.instantiateContext((err, context) ->
            if err
                return next(err)

            res.render("index", {
                ankorContextId: context.id
            })
        )

    debugMemoryStore: (req, res, next) ->
        data = {}
        for contextId, context of  @ankorService.ankor.store.contexts
            data[contextId] = {
                model: context.model,
                session: context.session
            }
        res.json(data)
