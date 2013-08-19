#Todo: Implement Path system for lists and maps
#Todo: Add config to types -> protected, local, default
#Todo: mark certain model types abstract so they can't be instantiated using instantiateModel
#Todo: Add name to type objects (for better debugging and error messages)?

###
Plan for today:
---------------
DONE Switch to non-persistent scheme
    DONE - Remove Store

DONE Switch to asynchronous response messages
    DONE - Remove CBs from listeners and so on and simply queue messages as outgoing

PROGRESS Disconnect ModelObject from ModelRef
    PROGRESS - Don't let models hold a ref

PROGRESS Make ModelObjects usable without Context (before inserted in a context)
    PROGRESS - No listeners when not inserted, ...

Remove toJson from ModelObject
    - Actually put ModelObject into ChangeMessage and let JsonMapper to the toJson part

DONE Think about a messageQueue class
    DONE - To remove incoming/outgoing messages from context
    DONE - To have events for new queued messages
    DONE- To be able to trigger message processing by these events
###

###
Findings of today:
------------------
Today I switched meanings of Model and ModelRef. Model's are stupid and only have get/set, and don't know about
their context or ref.

ModelRef's got more intelligent then, knowing their context and setting the values in the model objects in such
a way that listeners and messages are triggered...

How to work with the new style (setting up properties, listeners) can be seen in AppController.coffee, the
old style is still visible in AnkorService.coffee.

I like the old way better.

Therefore --> Making ModelRefs know their context is stupid, they should be stupid address objects that have some
tools. Given a context, they could return the object behind the path.

Probably I should add some intelligence to the ModelObjects then. If I find a way to calc the modelRef from a
model I don't need a model for set/get, which made it necessary to insert the model into an object forst before
working with it. This maybe could be achieved by using two extra fields:
    - isRoot: true/false
    - parent: null / { parentObj: MODELOBJ, propertyName: STRING }

When inserting I set the parent. Using the parent info I can go up in the tree and build the path for the modelRef.
###

assert = require("assert")
{isString, isArray, isObject} = require("underscore")
{Type, StringType, NumberType, BooleanType, ModelType, EnumType, ListType, MapType, createModelType} = require("./model/Type")
{TypeRegistry} = require("./model/TypeRegistry")
{ListenerRegistry} = require("./ListenerRegistry")
{ContextRegistry} = require("./ContextRegistry")
{Context} = require("./model/Context")
{ModelObject} = require("./model/ModelObject")
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
        @contextRegistry = new ContextRegistry()

        @transport = config?.transport        
        assert(@transport, "No transport defined")
        @transport.ankorSystem = @

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
        
        return new Context(@, new ModelObject(@rootType))

    onAction: (actionName, listener) ->
        return @listenerRegistry.registerActionListener(actionName, listener)

    onChange: (ref, listener) ->
        if ref not instanceof ModelRef
            ref = new ModelRef(ref)
        return @listenerRegistry.registerChangeListener(ref, listener)
