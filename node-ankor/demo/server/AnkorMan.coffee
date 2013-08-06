{ContextManager} = require("./base/ContextManager")
{AnkorService} = require("./service/AnkorService")
{AppController} = require("./controller/AppController")
{AppRouter} = require("./routes/AppRouter")

exports.AnkorMan = class AnkorMan
    constructor: ->
        @contextManager = new ContextManager()

        @contextManager.addObject("ankorService", AnkorService)
        @contextManager.addObject("appController", AppController)
        @contextManager.init()

    init: (app) ->
        @contextManager.addObject("appRouter", AppRouter, app)
        @contextManager.init()
