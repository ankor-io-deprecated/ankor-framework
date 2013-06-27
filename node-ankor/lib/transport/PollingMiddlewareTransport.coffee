###
# Connect Middleware
# 
# Requires: bodyParser Middleware
#
# Config:
# {
#   path: "/ankor"
#   contextResolver: function(req, cb) {
#       cb(CONTEXTID)
#   }
# }
###

assert = require("assert")

exports.PollingMiddlewareTransport = class PollingMiddlewareTransport
    constructor: (config) ->
        @path = config?.path
        @path ?= "/ankor"

        @contextResolver = config?.contextResolver
        #A default context resolver that takes 
        assert(@contextResolver, "PollingMiddlewareTransport requires a contextResolver")

    middleware: ->
        return (req, res, next) =>
            if req.method != "POST" or req.url != @path
                return next()

            @contextResolver(req, (err, contextId) =>
                response = {
                    contextId: contextId
                }
                res.end(JSON.stringify(response))
            )
