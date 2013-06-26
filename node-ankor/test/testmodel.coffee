Ankor = require("../lib/Ankor")

module.exports = ankor = new Ankor()

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

###
{
    userName: "Foo Bar",
    serverStatus: "Online",
    tabs: {
        "tab1": {
            id: "AnimalDetailTabModel",
            model: {
                animal: {
                    uuid: "asdf",
                    name: "Eagle",
                    type: "Fish/Bird/Mammal", //ENUM
                    family: "Esocidae/AccipitridaeBalaenopteridae/Felidae/Salmonidae" //ENUM
                },
                selectItems: {
                    types: [TYPEENUM],
                    families: [FAMILYENUM]
                }
            }
        },
        "tab2": {
            id: "AnimalSearchTabModel",
            model: {
                filter: {
                    name: "FilterName",
                    type: ENUM,
                    family: ENUM
                },
                selectItems: {
                    types: [TYPEENUM],
                    families: [FAMILYENUM]
                },
                animals: [
                    ANIMAL
                ]
            }
        }
    }
}

###