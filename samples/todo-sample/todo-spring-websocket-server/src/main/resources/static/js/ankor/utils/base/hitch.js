define(function() {
    return function(scope, method) {
        if (typeof method == "string") {
            method = scope[method];
        }
        return function() {
            return method.apply(scope, arguments || []);
        };
    };
});