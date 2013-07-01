{bind} = require("underscore")
Ankor = require("../../../lib/Ankor")
{ContextDepending} = require("../base/ContextDepending")

exports.AnkorService = class AnkorService extends ContextDepending
    constructor: ->
        super()

        @ankor = null

    init: ->
        @ankor = new Ankor({
            transport: new Ankor.transports.PollingMiddlewareTransport()
        })

        @ankor.defineEnum("AnimalType", [
            "Fish",
            "Bird",
            "Mammal"
        ])
        @ankor.defineEnum("AnimalFamily", [
            "Esocidae",
            "Accipitridae",
            "Balaenopteridae",
            "Felidae",
            "Salmonidae"
        ])
        @ankor.defineModel("Animal", {
            uuid: @ankor.STRING,
            name: @ankor.STRING,
            type: @ankor.TYPE("AnimalType"),
            family: @ankor.TYPE("AnimalFamily")
        })
        @ankor.defineModel("AnimalSelectItems", {
            types: @ankor.LIST(@ankor.TYPE("AnimalType")),
            families: @ankor.LIST(@ankor.TYPE("AnimalFamily"))
        })
        @ankor.defineModel("AnimalSearchFilter", {
            name: @ankor.STRING,
            type: @ankor.TYPE("AnimalType"),
            family: @ankor.TYPE("AnimalFamily")
        })
        @ankor.defineModel("AnimalTab", {
            id: @ankor.STRING
        })
        @ankor.extendModel("AnimalTab", "AnimalDetailTab", {
            animal: @ankor.TYPE("Animal"),
            selectItems: @ankor.TYPE("AnimalSelectItems")
        })
        @ankor.extendModel("AnimalTab", "AnimalSearchTab", {
            filter: @ankor.TYPE("AnimalSearchFilter"),
            selectItems: @ankor.TYPE("AnimalSelectItems"),
            animals: @ankor.LIST(@ankor.TYPE("Animal"))
        })
        @ankor.defineRoot({
            userName: @ankor.STRING,
            serverStatus: @ankor.STRING,
            tabs: @ankor.MAP(@ankor.TYPE("AnimalTab"))
        })

        @ankor.onAction("init", bind(@onInit, @))
        @ankor.onChange("/userName", bind(@onUsernameChange), @)

    onInit: (action, context, cb) ->
        context.model.set("userName", "Hello, World!")
        cb()

    onUsernameChange: (path, context, oldValue, newValue, cb) ->
        console.log("onUsernameChange", context.model.get("userName"), oldValue, newValue)
        cb()
