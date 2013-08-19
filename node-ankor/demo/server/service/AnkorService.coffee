{bind} = require("underscore")
ankor = require("../../../lib/ankor")
{ContextDepending} = require("../base/ContextDepending")

exports.AnkorService = class AnkorService extends ContextDepending
    constructor: ->
        super()

        @ankorSystem = null

    init: ->
        @ankorSystem = new ankor.AnkorSystem({
            transport: new ankor.transports.PollingMiddlewareTransport()
        })

        @ankorSystem.defineEnum("AnimalType", [
            "Fish",
            "Bird",
            "Mammal"
        ])
        @ankorSystem.defineEnum("AnimalFamily", [
            "Esocidae",
            "Accipitridae",
            "Balaenopteridae",
            "Felidae",
            "Salmonidae"
        ])
        @ankorSystem.defineModel("Animal", {
            uuid: @ankorSystem.STRING,
            name: @ankorSystem.STRING,
            type: @ankorSystem.TYPE("AnimalType"),
            family: @ankorSystem.TYPE("AnimalFamily")
        })
        ###@ankorSystem.defineModel("AnimalSelectItems", {
            types: @ankorSystem.LIST(@ankorSystem.TYPE("AnimalType")),
            families: @ankorSystem.LIST(@ankorSystem.TYPE("AnimalFamily"))
        })
        @ankorSystem.defineModel("AnimalSearchFilter", {
            name: @ankorSystem.STRING,
            type: @ankorSystem.TYPE("AnimalType"),
            family: @ankorSystem.TYPE("AnimalFamily")
        })
        @ankorSystem.defineModel("AnimalTab", {
            id: @ankorSystem.STRING
        })
        @ankorSystem.extendModel("AnimalTab", "AnimalDetailTab", {
            animal: @ankorSystem.TYPE("Animal"),
            selectItems: @ankorSystem.TYPE("AnimalSelectItems")
        })
        @ankorSystem.extendModel("AnimalTab", "AnimalSearchTab", {
            filter: @ankorSystem.TYPE("AnimalSearchFilter"),
            selectItems: @ankorSystem.TYPE("AnimalSelectItems"),
            animals: @ankorSystem.LIST(@ankorSystem.TYPE("Animal"))
        })###
        @ankorSystem.defineRoot({
            userName: @ankorSystem.STRING,
            serverStatus: @ankorSystem.STRING,
            #tabs: @ankorSystem.MAP(@ankorSystem.TYPE("AnimalTab"))
            animal: @ankorSystem.TYPE("Animal")
        })

        #@ankorSystem.onAction("init", bind(@onInit, @))
        #@ankorSystem.onChange("/*", bind(@onChange), @)

    onInit: (action, context, cb) ->
        context.model.set("userName", "Hello, World!")
        animal = context.createModelObject("Animal")
        context.model.set("animal", animal)
        animal.set("name", "test")
        cb()

    onChange: (ref, context, oldValue, newValue, cb) ->
        console.log("Change on", ref.getPath(), oldValue, newValue)
        cb()
