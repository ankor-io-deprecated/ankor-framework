uuid = require("node-uuid")

exports.Context = class Context
    constructor: (rootModel) ->
        @model = rootModel
        @attributes = {}
        @uuid = uuid.v4()

    save: ->
        #Should be called automatically after a listener executed, but also could be set manually
        #in case some other code changes something...
        console.log("Context#save() not implemented yet")


#Here i probably need a reference to the ankor instance I'm using
# so that I can access the store...
# and the nchor context should call saver after instantiateion