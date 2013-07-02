define([
    "dojo/_base/declare",
    "dojo/_base/array"
], function(declare, array) {
    var ModelRef = declare(null, {
        constructor: function(path) {
            this._path = [];
            var segments = path.split("/");
            for (var i = 0; i < segments.length; i++) {
                segment = segments[i];
                if (!segment) {
                    continue;
                }
                this._path.push(segment);
            }
        },
        getPath: function() {
            return "/" + this._path.join("/");
        },
        getSubPropertyRef: function(propertyName) {
            if (this.getPath() == "/") {
                return new ModelRef("/" + propertyName);
            }
            else {
                return new ModelRef(this.getPath() + "/" + propertyName);
            }
        },
        containsWildcard: function() {
            return (array.indexOf(this._path, "*") != -1);
        },
        matches: function(compareRef) {
            if (compareRef.containsWildcard()) {
                throw new Error("Ref to match can't contain wildcards");
            }
            else if (this.containsWildcard()) {
                var regexpString = "^" + this.getPath().replace(/\*/, ".*") + "$";
                var regexp = new RegExp(regexpString);
                return regexp.test(compareRef.getPath());
            }
            else {
                return (this.getPath() == compareRef.getPath());
            }
        },
        getBaseObject: function(rootModel) {
            var modelObject = rootModel;
            for (var i = 0, segment; (segment = this._path[i]); i++) {
                if (i == this._path.length - 1) {
                    break;
                }
                modelObject = modelObject.get(segment);
            }
            return modelObject;
        },
        getPropertyName: function() {
            if (this._path.length == 0) {
                return null;
            }
            return this._path[this._path.length - 1];
        }
    });
    return ModelRef;
});
