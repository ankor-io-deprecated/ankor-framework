define([
    "./BaseMessage"
], function(BaseMessage) {
    var ActionMessage = function(senderId, modelId, messageId, property, action) {
        BaseMessage.call(this, senderId, modelId, messageId, property);

        this.action = action;
    };
    ActionMessage.prototype = new BaseMessage();

    return ActionMessage;
});
