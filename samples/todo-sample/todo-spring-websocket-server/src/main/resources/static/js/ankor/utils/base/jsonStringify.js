define(function() {
    return function(jsonObject) {
        if (typeof JSON != "undefined") {
            return JSON.stringify((jsonObject));
        }
        else {
            var escapeString = function(str){
                return ('"' + str.replace(/(["\\])/g, '\\$1') + '"').
                    replace(/[\f]/g, "\\f").replace(/[\b]/g, "\\b").replace(/[\n]/g, "\\n").
                    replace(/[\t]/g, "\\t").replace(/[\r]/g, "\\r"); // string
            };
            var stringify = function(it){
                var val, objtype = typeof it;
                if(objtype == "number"){
                    return isFinite(it) ? it + "" : "null";
                }
                if(objtype == "boolean"){
                    return it + "";
                }
                if(it === null){
                    return "null";
                }
                if(typeof it == "string"){
                    return escapeString(it);
                }
                if(it instanceof Array){
                    var itl = it.length, res = [];
                    for(var key = 0; key < itl; key++){
                        var obj = it[key];
                        val = stringify(obj, key);
                        if(typeof val != "string"){
                            val = "null";
                        }
                        res.push(val);
                    }
                    return "[" + res.join(",") + "]";
                }
                var output = [];
                for(var key in it){
                    var keyStr;
                    if(it.hasOwnProperty(key)){
                        if(typeof key == "number"){
                            keyStr = '"' + key + '"';
                        }else if(typeof key == "string"){
                            keyStr = escapeString(key);
                        }else{
                            // skip non-string or number keys
                            continue;
                        }
                        val = stringify(it[key], key);
                        if(typeof val != "string"){
                            // skip non-serializable values
                            continue;
                        }
                        // At this point, the most non-IE browsers don't get in this branch
                        // (they have native JSON), so push is definitely the way to
                        output.push(keyStr + ":" + val);
                    }
                }
                return "{" + output.join(",") + "}"; // String
            };
            return stringify(jsonObject);
        }
    };
});