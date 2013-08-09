define(function() {
    var messageCounter = 0;

    var BaseTransport = function() {
        this.outgoingMessages = [];
        this.messageHandler = null;
    };

    BaseTransport.prototype.setMessageHandler = function(messageHandler) {
        this.messageHandler = messageHandler;
    };

    BaseTransport.prototype.sendMessage = function(message) {
        message.messageId = message.senderId + "#" + messageCounter++;
        this.outgoingMessages.push(message);
    };

    BaseTransport.prototype.processIncomingMessage = function(message) {
        if (this.messageHandler) {
            this.messageHandler(message);
        }
    };

    return BaseTransport;
});
