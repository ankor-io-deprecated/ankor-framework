{expect} = require("chai")
testmodel = require("../testmodel")
{Map} = require("../../lib/model/Map")

describe("Map", ->
    context = null
    beforeEach(->
        context = testmodel.instantiateContext()
    )
    describe("#getObject", ->
        it("should return a plain JS object for the stored map", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            data = {foo: "bar", hello: "world" }
            map.setObject(data)
            expect(map.getObject()).to.deep.equal(data)
        )
    )
    describe("#setObject", ->
        it("should accept only objects cotaining values of the defined data type", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            data = {foo: "bar", hello: 1 }
            expect(->
                map.setObject(data)
            ).to.throw(Error)
        )
    )
    describe("#count", ->
        it("should return the count of different keys of the map", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            expect(map.count()).to.equal(2)
        )
    )
    describe("#has", ->
        it("should return whether map contains given key", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            expect(map.has("foo")).to.be.true
            expect(map.has("bar")).to.be.false
        )
    )
    describe("#get", ->
        it("should return the value of a given key", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            expect(map.get("foo")).to.equal("bar")
        )
    )
    describe("#set", ->
        it("should insert a new given key and value", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            map.set("test", "works")
            expect(map.count()).to.equal(3)
            expect(map.get("test")).to.equal("works")
        )
        it("should update an existing key with the given value", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            map.set("foo", "works")
            expect(map.count()).to.equal(2)
            expect(map.get("foo")).to.equal("works")
        )
        it("should work with subclasses of the defined data type", ->
            animalDetailTab = testmodel.instantiateModel("AnimalDetailTab")
            tabs = context.model.get("tabs")
            tabs.set("tab1", animalDetailTab)
            expect(tabs.count()).to.equal(1)
            expect(tabs.get("tab1")).to.equal(animalDetailTab)
        )
    )
    describe("#remove", ->
        it("should remove a given key", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            map.remove("foo")
            expect(map.count()).to.equal(1)
            expect(->
                map.get("foo")
            ).to.throw(Error)
        )
        it("should throw an error for an invalid key", ->
            map = new Map(testmodel.MAP(testmodel.STRING))
            map.setObject({foo: "bar", hello: "world" })
            expect(->
                map.remove("test")
            ).to.throw(Error)
        )
    )
)
