assert = require("assert")
{isArray, clone} = require("underscore")

exports.List = class List
    constructor: (type) ->
        @type = type
        @_data = []

    getArray: ->
        return @_data

    setArray: (array) ->
        assert(isArray(array), "setArray only accepts arrays as value")
        for value in array
            assert(@type.type.validateValue(value), "Invalid setArray value '#{value}'")
        @_data = clone(array)

    length: ->
        return @_data.length

    pop: ->
        return @_data.pop()
    
    push: (value) ->
        assert(@type.type.validateValue(value), "Invalid push value '#{value}'")
        return @_data.push(value)

    shift: ->
        return @_data.shift()

    unshift: (value) ->
        assert(@type.type.validateValue(value), "Invalid unshift value '#{value}'")
        return @_data.unshift(value)
