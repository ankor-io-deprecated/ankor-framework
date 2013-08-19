{isString, isArray} = require("underscore")
{ModelObject} = require("./ModelObject")

exports.ModelRef = class ModelRef
    constructor: (context, path) ->
        @context = context

        if isString(path)
            @_path = []
            segments = path.split("/")
            for segment in segments
                if not segment
                    continue
                @_path.push(segment)
        else if isArray(path)
            @_path = path
        else
            throw new Error("ModelRef path has to be an array or a string")

    path: ->
        return "/" + @_path.join("/")

    propertyName: ->
        if @_path.length == 0
            return null
        return @_path[@_path.length - 1]

    parent: ->
        if @_path.length == 0
            throw new Error("Can't get parent of root ModelRef")

        return new ModelRef(@context, @_path.slice(0, -1))

    getValue: ->
        if @containsWildcard()
            throw new Error("Can't get the value of a wildcard ModelRef")

        value = @context.model
        for segment in @_path
            value = value.get(segment)
        return value

    setValue: (value) ->
        parentObject = @parent().getValue()
        if parentObject instanceof ModelObject
            parentObject.set(@propertyName(), value)
            #Todo: messaging + listeners
        else
            throw new Error("setValue called on a an invalid ref")







    


    

###
    getSubPropertyRef: (propertyName) ->
        if @getPath() == "/"
            return new ModelRef("/" + propertyName)
        else
            return new ModelRef(@getPath() + "/" + propertyName)

    containsWildcard: ->
        return (@_path.indexOf("*") != -1)

    matches: (compareRef) ->
        if compareRef.containsWildcard()
            throw new Error("Ref to match can't contain wildcards")
        else if @containsWildcard()###
#            regexpString = "^" + @getPath().replace(/\*/, ".*") + "$"
###            regexp = new RegExp(regexpString)
            return regexp.test(compareRef.getPath())
        else
            return @getPath() == compareRef.getPath()

    getBaseObject: (rootModel) ->
        modelObject = rootModel
        i = 0
        for segment in @_path
            if i == @_path.length -1
                break
            i++
            modelObject = modelObject.get(segment)
        return modelObject
###
