define(function() {
    var uuid = function() {
        //UUIDv4 generator from dojox/uuid
        var HEX_RADIX = 16;

        function _generateRandomEightCharacterHexString(){
            // Make random32bitNumber be a randomly generated floating point number
            // between 0 and (4,294,967,296 - 1), inclusive.
            var random32bitNumber = Math.floor( (Math.random() % 1) * Math.pow(2, 32) );
            var eightCharacterHexString = random32bitNumber.toString(HEX_RADIX);
            while(eightCharacterHexString.length < 8){
                eightCharacterHexString = "0" + eightCharacterHexString;
            }
            return eightCharacterHexString; // for example: "3B12F1DF"
        }

        var hyphen = "-";
        var versionCodeForRandomlyGeneratedUuids = "4"; // 8 == binary2hex("0100")
        var variantCodeForDCEUuids = "8"; // 8 == binary2hex("1000")
        var a = _generateRandomEightCharacterHexString();
        var b = _generateRandomEightCharacterHexString();
        b = b.substring(0, 4) + hyphen + versionCodeForRandomlyGeneratedUuids + b.substring(5, 8);
        var c = _generateRandomEightCharacterHexString();
        c = variantCodeForDCEUuids + c.substring(1, 4) + hyphen + c.substring(4, 8);
        var d = _generateRandomEightCharacterHexString();
        var returnValue = a + hyphen + b + hyphen + c + d;
        returnValue = returnValue.toLowerCase();
        return returnValue; // String
    };
    var hitch = function(scope, method) {
        if (typeof method == "string") {
            method = scope[method];
        }
        return function() {
            return method.apply(scope, arguments || []);
        };
    };
    var jsonParse = function(jsonString) {
        if (typeof JSON != "undefined") {
            return JSON.parse(jsonString)
        }
        else {
            return eval("(" + jsonString + ")");
        }
    };
    var jsonStringify = function(jsonObject) {
        if (typeof JSON != "undefined") {
            return JSON.stringify((jsonObject));
        }
        else {
            var undef;
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
    var xhrPost = function(url, data, cb) {
        var dataString = "";
        for (var key in data) {
            if (!data.hasOwnProperty(key)) {
                continue;
            }
            if (dataString.length != 0) {
                dataString += "&";
            }
            dataString += encodeURIComponent(key);
            dataString += "=";
            dataString += encodeURIComponent(data[key]);
        }

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState != 4) {
                return;
            }
            if (!xhr.response) {
                cb(new Error("xhr error"));
            }
            else {
                try {
                    var json = jsonParse(xhr.response);
                    cb(null, json)
                }
                catch (e) {
                    cb(e);
                }
            }
        };
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(dataString);
    };

    return {
        uuid: uuid,
        hitch: hitch,
        jsonParse: jsonParse,
        jsonStringify: jsonStringify,
        xhrPost: xhrPost
    };
});
