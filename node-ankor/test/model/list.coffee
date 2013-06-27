{expect} = require("chai")
ankor = require("../testmodel")

describe("List", ->
    context = null
    beforeEach((done) ->
        ankor.instantiateContext((err, createdContext) ->
            context = createdContext
            done(err)
        )
    )
    describe("#getArray", ->
        it("should return an array of values", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            expect(types.getArray()).to.deep.equal(["Fish", "Bird", "Mammal"])
        )
    )
    describe("#setArray", ->
        it("should only accept new arrays with only the defined data type", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            expect(->
                types.setArray([1, "foo", "bar"])
            ).to.throw(Error)
            expect(->
                types.setArray(ankor.TYPE("AnimalType").values)
            ).to.not.throw(Error)            
        )
    )
    describe("#length", ->
        it("should return the length of the list", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            expect(types.length()).to.equal(3)
        )
    )
    describe("#pop", ->
        it("should remove and return the last list element", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            expect(types.pop()).to.equal("Mammal")
            expect(types.length()).to.equal(2)
        )
    )
    describe("#push", ->
        it("should only accept values of the correct type and append it to the back", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            types.push("Fish")
            expect(types.length()).to.equal(4)
            expect(types.getArray()[3]).to.equal("Fish")
            expect(->
                types.push(3)
            ).to.throw(Error)
        )
    )
    describe("#shift", ->
        it("should remove and return the first list element", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            expect(types.shift()).to.equal("Fish")
            expect(types.length()).to.equal(2)
        )
    )
    describe("#unshift", ->
        it("should only accept values of the correct type and append it to the front", ->
            types = ankor.instantiateModel("AnimalSelectItems").get("types")
            types.setArray(ankor.TYPE("AnimalType").values)
            types.unshift("Mammal")
            expect(types.length()).to.equal(4)
            expect(types.getArray()[0]).to.equal("Mammal")
            expect(->
                types.unshift(3)
            ).to.throw(Error)
        )
    )
)