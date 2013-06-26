{extend} = require("underscore")
{ListType, MapType} = require("./Type")
{List} = require("./List")
{Map} = require("./Map")

exports.Model = class Model
    constructor: (type) ->
        @type = type

        #Build merged config based on all possiblty inherited configs
        @mergedConfig = {}
        mergeConfig = (type) =>
            if type.baseType
                mergeConfig(type.baseType)
            extend(@mergedConfig, type.config)
        mergeConfig(@type)

        #Instantiate properties based on merged config
        @_properties = {}
        for propertyName, type of @mergedConfig
            @_properties[propertyName] = null

            #Auto-instantiate lists
            if type instanceof ListType
                @_properties[propertyName] = new List(type)
            #Auto-instantiate maps
            if type instanceof MapType
                @_properties[propertyName] = new Map(type)

    get: (propertyName) ->
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        return @_properties[propertyName]

    set: (propertyName, value) ->
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        if not @mergedConfig[propertyName].validateValue(value)
            throw new Error("Invalid property value '#{value}'")

        @_properties[propertyName] = value

        