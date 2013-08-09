define([
    "./BaseMessage"
], function(BaseMessage) {
    var ChangeMessage = function(senderId, modelId, messageId, property, value) {
        BaseMessage.call(this, senderId, modelId, messageId, property);

        this.value = value;
    };
    ChangeMessage.prototype = new BaseMessage();

    return ChangeMessage;
});
