define(function() {
    var BaseTransport = function() {
        this.ankorSystem = null;
        this.outgoingMessages = [];
        this.messageHandler = null;
        this.messageCounter = 0;
    };

    BaseTransport.prototype.setMessageHandler = function(messageHandler) {
        this.messageHandler = messageHandler;
    };

    BaseTransport.prototype.sendMessage = function(message) {
        message.messageId = message.senderId + "#" + this.messageCounter++;
        this.outgoingMessages.push(message);
    };

    BaseTransport.prototype.processIncomingMessage = function(message) {
        if (this.messageHandler) {
            this.messageHandler(message);
        }
    };

    return BaseTransport;
});
