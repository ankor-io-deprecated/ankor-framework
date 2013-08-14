define(function() {
    var BaseTransport = function() {
        this.outgoingMessages = [];
        this.messageCounter = 0;
    };

    BaseTransport.prototype.init = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.utils = ankorSystem.utils;
    };

    BaseTransport.prototype.sendMessage = function(message) {
        message.messageId = message.senderId + "#" + this.messageCounter++;
        this.outgoingMessages.push(message);
        if (this.ankorSystem.debug) {
            console.log("OUT", message);
        }
    };

    BaseTransport.prototype.processIncomingMessage = function(message) {
        if (this.ankorSystem.debug) {
            console.log("IN", message);
        }
        this.ankorSystem.processIncomingMessage(message);
    };

    return BaseTransport;
});
