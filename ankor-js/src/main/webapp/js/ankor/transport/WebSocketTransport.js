define([
    "./BaseTransport"
], function (BaseTransport) {
    var WebSocketTransport = function (endpoint) {
        BaseTransport.call(this);

        this.endpoint = endpoint || "/websocket/ankor";
        this.isReady = false;

        /**
         * Sends pending messages after the connection has be established.
         */
        this.sendPendingMessages = function () {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            while (jsonMessages.length > 0) {
                var jsonMessage = jsonMessages.pop()
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

        var host = function (endpoint, clientId) {
            if (window.location.protocol == 'http:') {
                return 'ws://' + window.location.host + endpoint + '/' + clientId;
            } else {
                return 'wss://' + window.location.host + endpoint + '/' + clientId;
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

        this.socket = socket(host(this.endpoint, this.ankorSystem.senderId));
        if (this.socket != null) {

            var self = this;

            this.socket.onopen = function () {
                console.log('WebSocket connected');
                self.isReady = true;
                self.sendPendingMessages();
            };

            this.socket.onclose = function () {
                console.log('Info: WebSocket closed.');
                // TODO: Inform user
            };

            this.socket.onmessage = function (message) {
                console.log('WebSocket received messages');
                self.receiveIncomingMessage(message.data);
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
