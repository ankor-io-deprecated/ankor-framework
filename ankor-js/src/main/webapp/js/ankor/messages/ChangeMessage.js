define([
    "./BaseMessage"
], function(BaseMessage) {
    var ChangeMessage = function(senderId, modelId, messageId, property, type, key, value) {
        BaseMessage.call(this, senderId, modelId, messageId, property);

        this.type = type;
        this.key = key;
        this.value = value;
    };
    ChangeMessage.prototype = new BaseMessage();

    ChangeMessage.prototype.TYPES = {
        VALUE: "value",
        INSERT: "insert",
        DEL: "delete",
        REPLACE: "replace"
    };

    return ChangeMessage;
});
