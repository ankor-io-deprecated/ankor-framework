{extend} = require("underscore")
uuid = require("node-uuid")
{ModelType, ListType, MapType} = require("./Type")
{MapObject} = require("./MapObject")
{ListObject} = require("./ListObject")
{ChangeMessage} = require("../message/ChangeMessage")

exports.ModelObject = class ModelObject
    constructor: (type) ->
        @type = type

        #Build merged config based on all inherited configs
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

    get: (propertyName) ->
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        return @_properties[propertyName]

    set: (propertyName, value) ->
        #Validate call
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        if not @mergedConfig[propertyName].validateValue(value)
            throw new Error("Invalid property value '#{value}'")

        #Set value
        @_properties[propertyName] = value
