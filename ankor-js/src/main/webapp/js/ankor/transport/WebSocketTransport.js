define([
    "./BaseTransport"
], function (BaseTransport) {
    var WebSocketTransport = function (endpoint) {
        BaseTransport.call(this);

        this.endpoint = endpoint || "/websocket/ankor";
        this.isHandshake = false;
        this.isReady = false;

        /**
         * Sends pending messages after the connection has be established.
         * A new connection has a new clientId, so the pending messages have to have their senderId updated.
         *
         * @param clientId The uuid of the new connection.
         */
        this.sendPendingMessages = function (clientId) {
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

        var host = function (endpoint) {
            if (window.location.protocol == 'http:') {
                return 'ws://' + window.location.host + endpoint;
            } else {
                return 'wss://' + window.location.host + endpoint;
            }
        }

        var socket = function (host) {
            if ('WebSocket' in window) {
                return new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                return new MozWebSocket(host);
            } else {
                console.log('Error: WebSocket is not supported by this browser.');
                return null;
            }
        }

        this.socket = socket(host(this.endpoint));
        if (this.socket != null) {

            var self = this;

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
        }
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
