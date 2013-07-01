# 1. Make it work with MemoryStore Session & Ankor
# 2. Make it work with RedisStore Session & Ankor

###
I need three parts:
    - Transport
    - MessageResolver
    - Store

Store handles persisting and lookup by context uid as said...

Transport handles messaging -> technichal (socket.io, ...) + encode/decode

MessageResolver has to map a message to a context:
    Basically this means this is some code that looks in an express session for an ankoruid, loads it from the store, and processes the message for this context

- Think about how to auto-persist context after answering a message...
    -> I need to persist it after changing something in the context
    -> I need to persist it after changing somehting in the session info (session auto-saves on res.end)

    - I could autosave after any of the listeners (action, change) finished
    - So basically when I receive a change -> load context -> apply changes -> call listeners -> causes changes -> queues messages -> listener finished -> persist context

- Another idea: I could use the contextManager for all the configuration:
    - Specify well defined names, like "ankorTransport", "ankorStore", ... and "depend" on them in the ankor system...

###

###
Todo collection:

#Todo: Implement Path system for lists and maps
#Todo: Add config to types -> protected, local, default
#Todo: Enum also is a named type? therefore I could instantiateModel with an enum type? -> bad idea
#Todo: mark certain model types abstract so they can't be instantiated using instantiateModel
#Todo: Create Error subclasses for various thrown Errors (for get/set, ...)
#Todo: Add name to type objects (for better debugging and error messages)?
#Todo: RedisStore would have a problem with deserialization of extended Model where type is base model...
###

assert = require("assert")
{isString, isArray, isObject} = require("underscore")
{Type, StringType, NumberType, BooleanType, DateType, EnumType, ListType, MapType, createModelType} = require("./model/Type")
{TypeRegistry} = require("./model/TypeRegistry")
{ListenerRegistry} = require("./ListenerRegistry")
{Model} = require("./model/Model")
{Context} = require("./model/Context")
{MemoryStore} = require("./store/MemoryStore")
{PollingMiddlewareTransport} = require("./transport/PollingMiddlewareTransport")

module.exports = class Ankor
    #Type Accessors
    ###############
    STRING: new StringType()
    NUMBER: new NumberType()
    BOOLEAN: new BooleanType()
    DATE: new DateType()
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
        @validateConfig(config)
        assert(isString(name), "Model name has to be a string")

        type = createModelType(config)
        @typeRegistry.registerType(name, type)
        return type

    extendModel: (base, name, config) ->
        @validateConfig(config)
        assert(isString(name), "Model name has to be a string")

        baseType = @typeRegistry.getType(base)
        type = createModelType(config, baseType)
        @typeRegistry.registerType(name, type)
        return type

    defineRoot: (config) ->
        @validateConfig(config)
        assert(@rootType == null, "Root can only be set once")

        @rootType = createModelType(config)
        return @rootType

    validateConfig: (config)->
        assert(!@inited, "Model can't be changed after the first initContext call")
        assert(isObject(config), "Root config has to be an object")
        for propertyName, type of config
            assert(type instanceof Type, "Value of property #{propertyName} has to be a type object")
            assert(@typeRegistry.isTypeRegistered(type), "Given type is not registered with this instance")

    instantiateModel: (name) ->
        type = @typeRegistry.getType(name)
        return new Model(type)

    instantiateContext: (cb) ->
        @inited = true
        
        context = new Context(@)
        context.model = new Model(@rootType, context, "/")
        context.save((err) ->
            if err
                context = null
            if cb
                cb(err, context)
        )

    onAction: (actionName, listener) ->
        return @listenerRegistry.registerActionListener(actionName, listener)

    onChange: (path, listener) ->
        return @listenerRegistry.registerChangeListener(path, listener)

Ankor.transports = {
    PollingMiddlewareTransport: PollingMiddlewareTransport
}
