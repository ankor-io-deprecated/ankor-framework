{bind} = require("underscore")
{ContextDepending} = require("../base/ContextDepending")

exports.AppRouter = class AppRouter extends ContextDepending
    constructor: ->
        super()
        @dependOn([
            "ankorService",
            "appController"
        ])

    init: (app) ->
        app.get("/", bind(@index, @))

    index: (req, res, next) ->
        @ankorService.ankorSystem.createContext((err, context) =>
            if err
                return next(err)

            @appController.setupContext(context)
            req.session.ankorContextId = context.id
            res.render("index", {
                ankorContextId: context.id
            })
        )
