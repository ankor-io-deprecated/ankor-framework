define(function() {
    var PathSegment = function(key, type) {
        //Validate type
        var typeValid = false;
        for (var t in PathSegment.TYPE) {
            if (PathSegment.TYPE.hasOwnProperty(t) && type === PathSegment.TYPE[t]) {
                typeValid = true;
                break;
            }
        }
        if (!typeValid) {
            throw new Error("Illegal PathSegment type");
        }

        //Validate key
        if ((this.isProperty() && (!key || typeof key != "string")) || (this.isIndex() && typeof key != "number")) {
            throw new Error("Illegal PathSegment key");
        }

        //Init
        this.key = key;
        this.type = type;
    };

    PathSegment.TYPE = {
        PROPERTY: 0,
        INDEX: 1
    };

    PathSegment.prototype.isProperty = function() {
        return this.type === PathSegment.TYPE.PROPERTY;
    };

    PathSegment.prototype.isIndex = function() {
        return this.type === PathSegment.TYPE.INDEX;
    };

    return PathSegment;
});
