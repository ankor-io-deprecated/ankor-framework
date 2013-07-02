# 1. Make it work with MemoryStore Session & Ankor
# 2. Make it work with RedisStore Session & Ankor

#Todo: Implement Path system for lists and maps
#Todo: Add config to types -> protected, local, default
#Todo: mark certain model types abstract so they can't be instantiated using instantiateModel
#Todo: Add name to type objects (for better debugging and error messages)?

###
Latest thoughts:
----------------
Changing two parameters would make everything much easier:
    1. No CB for listeners -> listeners can cause queued messages at any time, they will be transfered to the client with the next transport slot
    2. This also requires, that the context is not persisted between message processing actions. Maybe this is a "requirement" for stateful servers anyways? Maybe persisting only is interesting for "saving/restoring" contexts manually on session end/restore or so... this requires sticky sessions...

The initial idea behind this design though was scalability...

Because with the current design, it would mean that I need a set/apply version with cb, so that if a local set causes
a local listener, which sets something again, ... would all happen within the store.load/persist...
And a cb for set/apply is kind of stupid now...
###

assert = require("assert")
{isString, isArray, isObject} = require("underscore")
{Type, StringType, NumberType, BooleanType, ModelType, EnumType, ListType, MapType, createModelType} = require("./model/Type")
{TypeRegistry} = require("./model/TypeRegistry")
{ListenerRegistry} = require("./ListenerRegistry")
{Context} = require("./model/Context")
{MemoryStore} = require("./store/MemoryStore")
{ModelRef} = require("./model/ModelRef")

exports.AnkorSystem = class AnkorSystem
    #Type Accessors
    ###############
    STRING: new StringType()
    NUMBER: new NumberType()
    BOOLEAN: new BooleanType()
    LIST: (type) ->
        assert(type instanceof Type, "Invalid type given for list")
        return new ListType(type)
    MAP: (type) ->
        assert(type instanceof Type, "Invalid type given for map")
        return new MapType(type)
    TYPE: (name) ->
        return @typeRegistry.getType(name)

    #Actual class
    #############
    constructor: (config) ->
        @inited = false
        @rootType = null
        
        @typeRegistry = new TypeRegistry()
        @listenerRegistry = new ListenerRegistry()
        @store = config?.store
        @transport = config?.transport        

        assert(@transport, "No transport defined")
        @transport.ankorSystem = @
        if not @store
            @store = new MemoryStore()

    defineEnum: (name, values) ->
        assert(!@inited, "Model can't be changed after the first initContext call")
        assert(isString(name), "Enum name has to be a string")
        assert(isArray(values), "Enum values has to be an array")
        for value in values
            assert(isString(value), "Enum value has to be a string")
        
        type = new EnumType(values)
        @typeRegistry.registerType(name, type)
        return type

    defineModel: (name, config) ->
        @_validateConfig(config)
        assert(isString(name), "Model name has to be a string")

        type = createModelType(config)
        @typeRegistry.registerType(name, type)
        return type

    extendModel: (base, name, config) ->
        @_validateConfig(config)
        assert(isString(name), "Model name has to be a string")

        baseType = @typeRegistry.getType(base)
        type = createModelType(config, baseType)
        @typeRegistry.registerType(name, type)
        return type

    defineRoot: (config) ->
        @_validateConfig(config)
        assert(@rootType == null, "Root can only be set once")

        @rootType = createModelType(config)
        return @rootType

    _validateConfig: (config)->
        assert(!@inited, "Model can't be changed after the first initContext call")
        assert(isObject(config), "Root config has to be an object")
        for propertyName, type of config
            assert(type instanceof Type, "Value of property #{propertyName} has to be a type object")
            assert(@typeRegistry.isTypeRegistered(type), "Given type is not registered with this instance")

    createContext: (cb) ->
        assert(@rootType, "Can't create context before root model was defined")
        @inited = true
        
        context = new Context(@)
        context.save((err) ->
            if err
                context = null
            if cb
                cb(err, context)
        )

    onAction: (actionName, listener) ->
        return @listenerRegistry.registerActionListener(actionName, listener)

    onChange: (ref, listener) ->
        if ref not instanceof ModelRef
            ref = new ModelRef(ref)
        return @listenerRegistry.registerChangeListener(ref, listener)
