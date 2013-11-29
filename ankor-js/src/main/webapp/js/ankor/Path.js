define([
    "./PathSegment"
], function(PathSegment) {
    var parseSegments = function(pathString) {
        var segments = [];
        var pathSegments = pathString.split(".");
        for (var i = 0, pathSegment; (pathSegment = pathSegments[i]); i++) {
            var keyIndex = pathSegment.indexOf("[");
            if (keyIndex == -1) {
                segments.push(new PathSegment(pathSegment, PathSegment.TYPE.PROPERTY));
            }
            else {
                var propertyName = pathSegment.substr(0, keyIndex);
                var keys = pathSegment.substring(keyIndex + 1, pathSegment.length - 1).split("][");

                segments.push(new PathSegment(propertyName, PathSegment.TYPE.PROPERTY));

                for (var j = 0, key; (key = keys[j]); j++) {
                    if (key.indexOf("'") != -1 || key.indexOf('"') != -1) {
                        segments.push(new PathSegment(key.substring(1, key.length -1), PathSegment.TYPE.PROPERTY));
                    }
                    else {
                        segments.push(new PathSegment(parseInt(key), PathSegment.TYPE.INDEX));
                    }
                }
            }
        }
        return segments;
    };

    var Path = function(pathOrSegments) {
        this.segments = [];

        if (pathOrSegments instanceof Array) {
            this.segments = pathOrSegments;
        }
        else if (typeof pathOrSegments == "string") {
            this.segments = parseSegments(pathOrSegments);
        }
    };

    Path.prototype.toString = function() {
        var path = "";
        for (var i = 0, segment; (segment = this.segments[i]); i++) {
            if (segment.type === PathSegment.TYPE.PROPERTY) {
                if (i != 0) {
                    path += ".";
                }
                path += segment.key;
            }
            else if (segment.type === PathSegment.TYPE.INDEX) {
                path += "[" + segment.key + "]";
            }
        }
        return path;
    };

    Path.prototype.append = function(path) {
        var segments = [];

        if (path instanceof Path) {
            segments = path.segments;
        }
        else if (path instanceof Array) {
            segments = path;
        }
        else if (typeof path == "string") {
            segments = parseSegments(path);
        }

        return new Path(this.segments.concat(segments));
    };

    Path.prototype.appendIndex = function(index) {
        var segments = this.segments.slice(0);
        segments.push(new PathSegment(index, PathSegment.TYPE.INDEX));
        return new Path(segments);
    };

    Path.prototype.parent = function() {
        return new Path(this.segments.slice(0, -1));
    };

    Path.prototype.propertyName = function() {
        return this.segments[this.segments.length - 1].key;
    };

    Path.prototype.equals = function(path) {
        return this.toString() == path.toString();
    };

    Path.prototype.slice = function(startIndex, lastIndex) {
        return new Path(this.segments.slice(startIndex, lastIndex));
    };

    return Path;
});
