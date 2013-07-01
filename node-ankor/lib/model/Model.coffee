{extend} = require("underscore")
uuid = require("node-uuid")
{ModelType, ListType, MapType} = require("./Type")
{List} = require("./List")
{Map} = require("./Map")
{ChangeMessage} = require("../message/ChangeMessage")

exports.Model = class Model
    constructor: (type, context=null, path=null) ->
        @type = type
        @context = context
        @path = path

        #Build merged config based on all possibility inherited configs
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
        #Apply value (and trigger listeners)
        @apply(propertyName, value)
        
        #Encode changes
        changeValue = value
        #Todo: deal with list and map
        if value and @mergedConfig[propertyName] instanceof ModelType
            changeValue = value.toJson()

        #Queue ChangeMessage
        @context.outgoingMessages.push(new ChangeMessage(uuid.v4(), "#{@path}#{propertyName}", changeValue))

    apply: (propertyName, value) ->
        #Validate call
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        if not @mergedConfig[propertyName].validateValue(value)
            throw new Error("Invalid property value '#{value}'")

        #Calc property path and set value
        propertyPath = "#{@path}#{propertyName}"
        oldValue = @_properties[propertyName]
        @_properties[propertyName] = value

        #Set child path is property is a Model, List or Map
        if @mergedConfig[propertyName] instanceof ModelType
            value.context = @context
            value.path = "#{propertyPath}/"

        #Trigger listeners
        @context.ankorSystem.listenerRegistry.triggerChangeEvent(@context, propertyPath, oldValue, value)

    toJson: ->
        plainObject = {}
        for propertyName, type of @mergedConfig
            if type instanceof ModelType
                plainObject[propertyName] = @_properties[propertyName].toJson()
            else
                plainObject[propertyName] = @_properties[propertyName]
        return plainObject

        