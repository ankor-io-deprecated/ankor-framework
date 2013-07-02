assert = require("assert")
{isString, isNumber, isBoolean, isDate, isNull} = require("underscore")

exports.Type = class Type
    __builtin__: true,

    constructor: ->
    validateValue: ->
        throw new Error("validateValue not implemented for type")

exports.EnumType = class EnumType extends Type
    constructor: (values) ->
        @values = values
    validateValue: (value) ->
        return isNull(value) or value in @values

exports.ListType = class ListType extends Type
    constructor: (type) ->
        @type = type
    validateValue: (value) ->
        return value.type instanceof @constructor and value.type.type == @type

exports.MapType = class MapType extends Type
    constructor: (type) ->
        @type = type

exports.StringType = class StringType extends Type
    constructor: ->
    validateValue: (value) ->
        return isNull(value) or isString(value)

exports.NumberType = class NumberType extends Type
    constructor: ->
    validateValue: (value) ->
        return isNull(value) or isNumber(value)

exports.BooleanType = class BooleanType extends Type
    constructor: ->
    validateValue: (value) ->
        return isNull(value) or isBoolean(value)

exports.ModelType = class ModelType extends Type
    __builtin__: false,

    constructor: (config, baseType) ->
        @config = config
        @baseType = baseType
    validateValue: (value) ->
        return isNull(value) or value.type instanceof @constructor

#################################################################

exports.createModelType = (config, baseType) ->
    baseClass = ModelType
    if baseType
        baseClass = baseType.constructor

    typeConstructor = class extends baseClass
        constructor: ->
            super

    return new typeConstructor(config, baseType)
