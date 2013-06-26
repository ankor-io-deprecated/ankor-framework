{isFunction} = require("underscore")
{ContextDepending} = require("./ContextDepending")

exports.ContextManager = class ContextManager
    constructor: ->
        @registry = {}
        @objectsToInitialize = {}

    addObject: (name, constructor, initParams...) ->
        if name of @registry
            throw new Error("ContextManager already contains an object named #{name}")
            
        @registry[name] = new constructor()
        @objectsToInitialize[name] = initParams

    init: ->
        #Inject dependencies for ContextDependings
        for name, initParams of @objectsToInitialize
            obj = @registry[name]
            if obj instanceof ContextDepending
                for dependency in obj.contextDependencies
                    if dependency not of @registry
                        throw new Error("Context dependency #{dependency} not met for object #{name}")
                    obj[dependency] = @registry[dependency]

        #Create init order (circular dependcies are not supported)
        #http://en.wikipedia.org/wiki/Topological_sorting
        initOrder = []
        unmarked = (name for name, initParams of @objectsToInitialize)
        marked = {}

        visit = (obj) =>
            if marked[obj] == "TEMP"
                throw new Error("Circular context dependency found: #{obj}")
            if obj not of marked
                unmarked = (name for name in unmarked when name != obj)
                marked[obj] = "TEMP"
                if @registry[obj].contextDependencies
                    for dependency in @registry[obj].contextDependencies
                        if dependency not in unmarked and dependency not of marked then continue #already initialized, not part of the graph we are calculating here...
                        visit(dependency)
                marked[obj] = "PERM"
                initOrder.push(obj)

        while unmarked.length > 0
            visit(unmarked[0])

        console.log("ContextManager Init order:", initOrder)

        #Init with initParams
        for name in initOrder
            initParams = @objectsToInitialize[name]
            obj = @registry[name]
            if obj instanceof ContextDepending
                obj.contextManager = @
            if isFunction(obj.init)
                obj.init(initParams...)

        @objectsToInitialize = {}

    #Can be used to inject dependencies from this context into "anonymous" objects that are not part of
    #the context itself (page objects for example)
    initContextDepending: (constructor) ->
        obj = new constructor()
        obj.contextManager = @
        
        if obj not instanceof ContextDepending
            throw new Error("initContextDepending called on non ContextDepending object")

        for dependency in obj.contextDependencies
            if dependency not of @registry
                throw new Error("Context dependency #{dependency} not met for ContextDepending object #{obj}")
            obj[dependency] = @registry[dependency]

        return obj
