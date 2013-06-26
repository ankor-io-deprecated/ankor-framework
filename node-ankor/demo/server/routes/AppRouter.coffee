{bind} = require("underscore")
{ContextDepending} = require("../base/context/ContextDepending")

exports.AppRouter = class AppRouter extends ContextDepending
    constructor: ->
        super()

    init: (app) ->
        app.get("/", bind(@index, @))

    index: (req, res, next) ->
        #Create Ankor context for this new request
        #ankorContext = ...

        #Provide template with contextId which has to be given to the Client side script?
        #templateContext = { ankorContextId: ankorContext.getId() }
