define(function() {
    return function(jsonString) {
        if (typeof JSON != "undefined") {
            return JSON.parse(jsonString)
        }
        else {
            return eval("(" + jsonString + ")");
        }
    };
});
