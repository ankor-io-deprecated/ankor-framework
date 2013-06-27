# 1. Make it work with MemoryStore Session & Ankor
# 2. Make it work with RedisStore Session & Ankor

#   DONE - 1. First create the model defintion itself (like sequelize and stateful)
#   DONE - 2. Create the model instantiation
#   3. Add server side event handling with path and value that changed and so on?
#   4. Think about the model event transport - maybe one abstract layer above socket.io?
#   5. Only then think about socket.io
#   6. And when doing so, think about persisting/restoring model instances
#   7. And when doing so, also think about express-redis-session-socket-io integration for the demo, but that should be fully optional

###
Thoughts regarding the next steps:

A basic decision I have to make is whether a context lives only on once instance, or on multiple instances.

1 - If it lives on one instance only I have to use sticky sessions or so to make it work, and keep the context
in memory between requests.

2 - If it lives on multiple instances I have to make the context serializable and e.g. save it with the session.

In both cases, I have to think about the glue code that associates a session with a context AND how to
deal with event listeners.
Using 2) I could attach my listeners to the context instane itself and therefore remember my session as
closures.
Using 1) I have to use global listeners, which get the session/context as parameters.

Also compare to socket.io - i think they have sticky sessions (based on their transport mechanism) and use
redis for cross-instance communication. Since I plan to use socket.io for transport (at least one possible)
transport as well, I'm probably also safe to use sticky instances...
I therefore don't even have to implement the serialization and so on?
But also check how socket.io works together with express sessions -> I probably can learn somthing from this
as well. Because if I use a polling transport, I can use the express session directly, for socket.io I have to
make the conncetion to the session somehow...

But maybe Ankor could be used without express (or a different framework), so don't be directly dependent
on a session object.

Maybe all the good stuff can be combined somehow... Global listeners (to only have setup them once) and
but instance based events with session? Maybe the "modelHolder" Manfred mentioned is an abstract concept
that I could use as well.

-> Idea: initialize Ankot with a "Store" object. Default implementation would be a "MemoryStore" that keeps the
context references in memory. Another implementation could be a "RedisStore", which would have to serialize
the context. A JSONSerializer could be a separate Class in the namespace of this stores that could be shared
between Stores.
This also means that I will have global listeners, that get the context and the path and the value as parameters.
The context can be fetched via the Store wthin ankor...
I also should introduce a context with a uuid per context, which then is sent together with the actions. and used for
lookup in the stores...

--

I need three parts:
    - Transport
    - MessageResolver
    - Store

Store handles persisting and lookup by context uid as said...

Transport handles messaging -> technichal (socket.io, ...) + encode/decode

MessageResolver has to map a message to a context:
    Basically this means this is some code that looks in an express session for an ankoruid, loads it from the store, and processes the message for this context

- probably i should create a private part of an anchorContext that (like the express session) in which I can store a
per anchorContext infos (like loggedIn user) -> also important for multiwindow (as supported per contextId)

- Think about how to auto-persist context after answering a message...
    -> I need to persist it after changing something in the context
    -> I need to persist it after changing somehting in the session info (session auto-saves on res.end)

    - I could autosave after any of the listeners (action, change) finished
    - So basically when I receive a change -> load context -> apply changes -> call listeners -> causes changes -> queues messages -> listener finished -> persist context

###

###
Todo collection:

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
        @store = config?.store
        @transport = config?.transport        

        assert(@transport, "No transport defined")
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
        
        context = new Context(@, new Model(@rootType))
        context.save((err) ->
            if err
                context = null
            if cb
                cb(err, context)
        )

Ankor.transports = {
    PollingMiddlewareTransport: PollingMiddlewareTransport
}
