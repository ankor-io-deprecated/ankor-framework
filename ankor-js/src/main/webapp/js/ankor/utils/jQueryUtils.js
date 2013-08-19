define([
    "./base/uuid",
    "./base/jsonStringify"
], function(uuid, jsonStringify) {
    return function(jquery) {
        this.uuid = uuid;
        this.hitch = function(scope, method) {
            if (typeof method == "string") {
                method = scope[method];
            }
            return jquery.proxy(method, scope);
        };
        this.jsonParse = jquery.parseJSON;
        this.jsonStringify = jsonStringify;
        this.xhrPost = function(url, data, cb) {
            jquery.post(url, data, function(data) {
                cb(null, data);
            }, "text").fail(function() {
                cb(new Error("xhr error"));
            });
        };
    };
});