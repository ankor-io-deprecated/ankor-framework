{ContextManager} = require("./base/ContextManager")
{AnkorService} = require("./service/AnkorService")
{AppRouter} = require("./routes/AppRouter")

exports.AnkorMan = class AnkorMan
    constructor: ->
        @contextManager = new ContextManager()

        @contextManager.addObject("ankorService", AnkorService)
        @contextManager.init()

    init: (app) ->
        @contextManager.addObject("appRouter", AppRouter, app)
        @contextManager.init()
