exports.TypeRegistry = class TypeRegistry
    constructor: ->
        @typeMap = {}
        @typeList = []

    registerType: (name, type) ->
        if name of @typeMap
            throw new Error("Type with name #{name} already exists")
        @typeMap[name] = type
        @typeList.push(type)

    getType: (name) ->
        if name not of @typeMap
            throw new Error("Type with name #{name} doesn't exist")
        return @typeMap[name]

    isTypeRegistered: (type) ->
        if type.__builtin__
            return true
        else
            return type in @typeList
