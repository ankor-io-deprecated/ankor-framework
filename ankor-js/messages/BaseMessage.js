define(function() {
    var BaseMessage = function(senderId, modelId, messageId, property) {
        this.senderId = senderId || null;
        this.modelId = modelId || null;
        this.messageId = messageId || null;
        this.property = property || null;
    };

    return BaseMessage;
});