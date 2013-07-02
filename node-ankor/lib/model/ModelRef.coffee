exports.ModelRef = class ModelRef
    constructor: (path) ->
        @_path = []
        segments = path.split("/")
        for segment in segments
            if not segment
                continue
            @_path.push(segment)

    getPath: ->
        return "/" + @_path.join("/")

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
        else if @containsWildcard()
            regexpString = "^" + @getPath().replace(/\*/, ".*") + "$"
            regexp = new RegExp(regexpString)
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

    getPropertyName: ->
        if @_path.length == 0
            return null
        return @_path[@_path.length - 1]
        