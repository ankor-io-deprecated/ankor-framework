{extend} = require("underscore")
uuid = require("node-uuid")
{ModelType, ListType, MapType} = require("./Type")
{MapObject} = require("./MapObject")
{ListObject} = require("./ListObject")
{ChangeMessage} = require("../message/ChangeMessage")

exports.ModelObject = class ModelObject
    constructor: (type, context, ref=null) ->
        @type = type
        @context = context
        @ref = ref

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
        if not @ref
            throw new Error("Can't set model value before model was inserted into a context")

        #Apply value (and trigger listeners)
        @apply(propertyName, value)
        
        #Create a ChangeMessage and queue it
        propertyRef = @ref.getSubPropertyRef(propertyName)
        propertyType = @mergedConfig[propertyName]        
        changeValue = value
        if value and (propertyType instanceof ModelType or propertyType instanceof MapType or propertyType instanceof ListType)
            changeValue = value.toJson()

        @context.outgoingMessages.push(new ChangeMessage(uuid.v4(), @ref.getSubPropertyRef(propertyName), changeValue))

    apply: (propertyName, value) ->
        #Validate call
        if propertyName not of @_properties
            throw new Error("Invalid property name '#{propertyName}'")
        if not @mergedConfig[propertyName].validateValue(value)
            throw new Error("Invalid property value '#{value}'")

        #Prepare values
        propertyRef = @ref.getSubPropertyRef(propertyName)
        propertyType = @mergedConfig[propertyName]
        oldValue = @_properties[propertyName]
        if value and (propertyType instanceof ModelType or propertyType instanceof MapType or propertyType instanceof ListType)
            value.ref = propertyRef

        #Apply and trigger listeners
        @_properties[propertyName] = value
        @context.ankorSystem.listenerRegistry.triggerChangeEvent(@context, propertyRef, oldValue, value)
        
        #Trigger recursivley if needed
        if value and (propertyType instanceof ModelType or propertyType instanceof MapType or propertyType instanceof ListType)
            value.applyRecursive()

    applyRecursive: ->
        for propertyName, value of @_properties
            @apply(propertyName, value)

    toJson: ->
        plainObject = {}
        for propertyName, type of @mergedConfig
            if type instanceof ModelType or type instanceof MapType or type instanceof ListType
                plainObject[propertyName] = @_properties[propertyName].toJson()
            else
                plainObject[propertyName] = @_properties[propertyName]
        return plainObject

        