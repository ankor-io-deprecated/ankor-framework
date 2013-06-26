{isArray} = require("underscore")

exports.ContextDepending = class ContextDepending
    constructor: ->
        @contextManager = null
        @contextDependencies = []

    #To be called during constructor to add context dependencies
    dependOn: (dependencies) ->
        if not isArray(dependencies)
            dependencies = [ dependencies ]
        @contextDependencies = @contextDependencies.concat(dependencies)

    #Called after context dependencies have been injected
    init: ->
