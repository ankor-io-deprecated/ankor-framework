{bind} = require("underscore")
{ContextDepending} = require("../base/ContextDepending")

exports.AppRouter = class AppRouter extends ContextDepending
    constructor: ->
        super()
        @dependOn("ankorService")

    init: (app) ->
        app.get("/", bind(@index, @))

    index: (req, res, next) ->
        @ankorService.ankor.instantiateContext((err, context) ->
            if err
                return next(err)

            req.session.ankorContextId = context.id
            res.render("index", {
                ankorContextId: context.id
            })
        )
