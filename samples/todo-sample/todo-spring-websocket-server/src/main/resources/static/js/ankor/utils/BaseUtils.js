define([
    "./base/uuid",
    "./base/hitch",
    "./base/jsonParse",
    "./base/jsonStringify",
    "./base/xhrPost",
], function(uuid, hitch, jsonParse, jsonStringify, xhrPost) {
    return function() {
        this.uuid = uuid;
        this.hitch = hitch;
        this.jsonParse = jsonParse;
        this.jsonStringify = jsonStringify;
        this.xhrPost = xhrPost;
    };
});
