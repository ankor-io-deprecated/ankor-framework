{expect} = require("chai")
ankor = require("../testmodel")
{List} = require("../../lib/model/List")

describe("Model", ->
    context = null
    beforeEach(->
        context = ankor.instantiateContext()
    )
    describe("#constructor", ->
        it("should automatically instantiate list properties", ->
            selectItems = ankor.instantiateModel("AnimalSelectItems")
            expect(selectItems.get("types")).to.be.an.instanceof(List)
        )
    )
    describe("#get", ->
        it("should throw an error when getting unknown property", ->
            expect(->
                context.model.get("fooBar")
            ).to.throw(Error)
        )
    )
    describe("#set", ->
        # STRING TESTS #
        ################
        it("should accept strings for StringType properties", ->
            context.model.set("userName", "test")
            expect(context.model.get("userName")).to.equal("test")
        )
        it("should throw for non strings for StringType properties", ->
            expect(->
                context.model.set("userName", 2)
            ).to.throw(Error)
        )
        it("should not throw when setting null", ->
            context.model.set("userName", "sschuster")
            context.model.set("userName", null)
            expect(context.model.get("userName")).to.be.null
        )

        # ENUM TESTS #
        ##############
        it("should accept defined enum value for EnumType", ->
            animal = ankor.instantiateModel("Animal")
            animal.set("type", "Fish")
            expect(animal.get("type")).to.equal("Fish")
        )
        it("should throw for undefined enum value for EnumType", ->
            animal = ankor.instantiateModel("Animal")
            expect(->
                animal.set("family", "Fish")
            ).to.throw(Error)
        )

        # LIST TESTS #
        ##############
        it("should not accept null as a value for ListType", ->
            selectItems = ankor.instantiateModel("AnimalSelectItems")
            expect(->
                selectItems.set("types", null)
            ).to.throw(Error)
        )
        it("should allow to set a new list of the same type only for ListType", ->
            selectItems = ankor.instantiateModel("AnimalSelectItems")
            expect(->
                selectItems.set("types", new List(ankor.LIST(ankor.STRING)))
            ).to.throw(Error)
            expect(->
                selectItems.set("types", new List(ankor.LIST(ankor.TYPE("AnimalType"))))
            ).to.not.throw(Error)
        )

        # INHERITED TESTS #
        ###################
        it("should be possible to set values of inherited base models", ->
            animal = ankor.instantiateModel("Animal")
            animalDetailTab = ankor.instantiateModel("AnimalDetailTab")
            animalDetailTab.set("id", "foo")
            animalDetailTab.set("animal", animal)
            expect(animalDetailTab.get("id")).to.equal("foo")
            expect(animalDetailTab.get("animal")).to.equal(animal)
        )
    )
)