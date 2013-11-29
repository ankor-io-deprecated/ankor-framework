define(function() {
    var Message = function(senderId, modelId, messageId, event) {
        this.senderId = senderId || null;
        this.modelId = modelId || null;
        this.messageId = messageId || null;
        this.event = event || null;
    };

    return Message;
});