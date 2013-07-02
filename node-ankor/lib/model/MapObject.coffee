assert = require("assert")
{isObject, isString, clone} = require("underscore")

exports.Map = class Map
    constructor: (type) ->
        @type = type
        @_data = {}
        @_count = 0

    getObject: ->
        return @_data

    setObject: (obj) ->
        assert(isObject(obj), "setObject only accepts objects as value")
        count = 0
        for key, value of obj
            assert(isString(key), "Invalid setObject key '#{key}'")
            assert(@type.type.validateValue(value), "Invalid setObject value '#{value}'")
            count++
        @_data = clone(obj)
        @_count = count

    count: ->
        return @_count

    has: (key) ->
        return key of @_data

    get: (key) ->
        assert(@has(key), "get didn't find key '#{key}'")
        return @_data[key]

    set: (key, value) ->
        assert(@type.type.validateValue(value), "Invalid set value '#{value}'")
        hadBefore = @has(key)
        @_data[key] = value
        if not hadBefore
            @_count++

    apply: (key, value) ->
        pass
    
    remove: (key) ->
        assert(@has(key), "remove didn't find key '#{key}'")
        delete @_data[key]
        @_count--

    applyRemove: (key) ->
        pass
