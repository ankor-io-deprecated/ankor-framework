define([
    "dojox/uuid/generateRandomUuid",
    "dojo/_base/lang",
    "dojo/json",
    "dojo/request/xhr"
], function(uuid, lang, json, xhr) {
    return function(jquery) {
        this.uuid = uuid;
        this.hitch = lang.hitch;
        this.jsonParse = json.parse;
        this.jsonStringify = json.stringify;
        this.xhrPost = function(url, data, cb) {
            xhr.post(url, {
                data: data,
                handleAs: "text",
            }).then(function(response) {
                    cb(null, response);
            }, function(err) {
                    cb(err);
            });
        };
    };
});