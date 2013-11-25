define([
    "./BaseEvent"
], function(BaseEvent) {
    var ChangeEvent = function(path, eventSource, type, key, value) {
        BaseEvent.call(this, path, eventSource);

        this.type = type;
        this.key = key;
        this.value = value;
    };

    ChangeEvent.prototype = new BaseEvent();

    ChangeEvent.prototype.TYPE = ChangeEvent.TYPE = {
        VALUE: "value",
        INSERT: "insert",
        DEL: "delete",
        REPLACE: "replace"
    };

    return ChangeEvent;
});