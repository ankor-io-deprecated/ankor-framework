{expect} = require("chai")
ankor = require("../testmodel")

describe("MemoryStore", ->
    context = null
    beforeEach(->
        context = ankor.instantiateContext()
    )
    describe("#save", ->
        it("should save a given context object", (done) ->
            ankor.store.save(context, done)
        )
        it("should throw an error if given something else than a Context", ->
            expect(->
                ankor.store.save("fooBar")
            ).to.throw(Error)
        )
    )
    describe("#load", ->
        it("should load the given context", (done) ->
            ankor.store.save(context, (err) ->
                if err then return done(err)
                ankor.store.load(context.uuid, (err, loadedContext) ->
                    expect(loadedContext).to.deep.equal(context)
                    done(err)
                )
            )
        )
        it("should return an error if given an invalid contextUuid", (done) ->
            ankor.store.load("asdf", (err, loadedContext) ->
                expect(err).to.be.an.instanceof(Error)
                expect(loadedContext).to.not.exist
                done()
            )
        )
    )
)