define([
    "./BaseTransport"
], function (BaseTransport) {
    var WebSocketTransport = function (endpoint, options) {
        BaseTransport.call(this);
        this.endpoint = endpoint;

        this.isHandshake = false;
        this.isReady = false;

        /**
         * Sends pending messages after the connection has be established.
         * A new connection has a new clientId, so the pending messages have to have their senderId updated.
         *
         * @param clientId The uuid of the new connection.
         */
        this.sendPendingMessages = function(clientId) {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            while (jsonMessages.length > 0) {
                var jsonMessage = jsonMessages.pop()
                jsonMessage.senderId = clientId;
                this.sendMessageInner(jsonMessage);
            }
            this.outgoingMessages = [];
        }

        /**
         * Private method to prevent code duplication.
         */
        this.sendMessageInner = function (jsonMessage) {
            var msg = this.utils.jsonStringify(jsonMessage)
            console.log('WebSocket send message ', msg);
            this.socket.send(msg);
        }
    };

    WebSocketTransport.prototype = new BaseTransport();

    WebSocketTransport.prototype.init = function (ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        var self = this;

        var host = "";
        if (window.location.protocol == 'http:') {
            host = 'ws://' + window.location.host + this.endpoint;
        } else {
            host = 'wss://' + window.location.host + this.endpoint;
        }

        if ('WebSocket' in window) {
            this.socket = new WebSocket(host);
        } else if ('MozWebSocket' in window) {
            this.socket = new MozWebSocket(host);
        } else {
            console.log('Error: WebSocket is not supported by this browser.');
            return;
        }

        this.socket.onopen = function () {
            console.log('WebSocket connected');
            self.isHandshake = true;
        };

        this.socket.onclose = function () {
            console.log('Info: WebSocket closed.');
        };

        this.socket.onmessage = function (message) {
            console.log('WebSocket received messages');

            if (self.isHandshake && !self.isReady) {
                // TODO: Check if UUID
                var clientId = message.data;
                self.ankorSystem.senderId = clientId;
                self.sendPendingMessages(clientId)
                self.isHandshake = false;
                self.isReady = true;
            } else if (self.isReady && !self.isHandshake) {
                self.receiveIncomingMessage(message.data);
            }
        };
    };

    WebSocketTransport.prototype.sendMessage = function (message) {
        BaseTransport.prototype.sendMessage.call(this, message);
        if (this.isReady) {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            this.sendMessageInner(jsonMessages.pop());
            this.outgoingMessages = [];
        }
    };

    return WebSocketTransport;
});
