define(function() {
    var Message = function(senderId, modelId, messageId, event) {
        this.senderId = senderId || null; // TODO remove
        this.modelId = modelId || null; // TODO remove
        this.messageId = messageId || null; // TODO remove
        this.event = event || null;
    };

    return Message;
});