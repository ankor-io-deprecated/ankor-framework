{bind} = require("underscore")
{ContextDepending} = require("../base/ContextDepending")

exports.AppRouter = class AppRouter extends ContextDepending
    constructor: ->
        super()

    init: ->

    setupContext: (context) ->
        userNameRef = context.createModelRef("/userName")
        userNameRef.addChangeListener(bind(@onUserNameChange, @))

        userNameRef.set("Hello World")

        animal = context.createModelObject("Animal")
        animal.set("name", "foo")
        context.createModelRef("/animal").set(animal)

    onUserNameChange: (context, ref, oldValue, newValue) ->
        console.log("Username changed from #{oldValue} to #{newValue}")
