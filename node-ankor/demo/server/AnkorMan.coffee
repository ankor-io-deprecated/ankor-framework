{ContextManager} = require("./base/ContextManager")
{AppRouter} = require("./routes/AppRouter")

exports.AnkorMan = class AnkorMan
    constructor: ->
        @contextManager = new ContextManager()

    init: (app) ->
        @contextManager.addObject("appRouter", AppRouter, app)



###Ankor = require("../../lib/Ankor")

#Define the model
ankor = new Ankor()

ankor.defineEnum("AnimalType", [
    "Fish",
    "Bird",
    "Mammal"
])
ankor.defineEnum("AnimalFamily", [
    "Esocidae",
    "Accipitridae",
    "Balaenopteridae",
    "Felidae",
    "Salmonidae"
])
ankor.defineModel("Animal", {
    uuid: ankor.STRING,
    name: ankor.STRING,
    type: ankor.TYPE("AnimalType"),
    family: ankor.TYPE("AnimalFamily")
})
ankor.defineModel("AnimalSelectItems", {
    types: ankor.LIST(ankor.TYPE("AnimalType")),
    families: ankor.LIST(ankor.TYPE("AnimalFamily"))
})
ankor.defineModel("AnimalSearchFilter", {
    name: ankor.STRING,
    type: ankor.TYPE("AnimalType"),
    family: ankor.TYPE("AnimalFamily")
})
ankor.defineModel("AnimalTab", {
    id: ankor.STRING
})
ankor.extendModel("AnimalTab", "AnimalDetailTab", {
    animal: ankor.TYPE("Animal"),
    selectItems: ankor.TYPE("AnimalSelectItems")
})
ankor.extendModel("AnimalTab", "AnimalSearchTab", {
    filter: ankor.TYPE("AnimalSearchFilter"),
    selectItems: ankor.TYPE("AnimalSelectItems"),
    animals: ankor.LIST(ankor.TYPE("Animal"))
})
ankor.defineRoot({
    userName: ankor.STRING,
    serverStatus: ankor.STRING,
    tabs: ankor.MAP(ankor.TYPE("AnimalTab"))
})

#Instantiate a context
context = ankor.instantiateContext()###