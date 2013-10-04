define([
    "jquery",
    "./BaseTransport",
    "gracefulWebSocket"             //Require only, no reference needed
], function ($, BaseTransport) {
    var WebSocketTransport = function (endpoint, options) {
        BaseTransport.call(this);

        var _options = options || {}
        this.endpoint = endpoint || "/websocket/ankor";
        this.heartbeatInterval = _options.heartbeatInterval || 25000;

        var _isReady = false;

        /**
         * Sends pending messages after the connection has be established.
         */
        this._sendPendingMessages = function () {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            while (jsonMessages.length > 0) {
                var jsonMessage = jsonMessages.pop()
                this._sendMessageInner(jsonMessage);
            }
            this.outgoingMessages = [];
        }

        /**
         * Private method to prevent code duplication.
         */
        this._sendMessageInner = function (jsonMessage) {
            var msg = this.utils.jsonStringify(jsonMessage)
            console.log('WebSocket send message ', msg);
            this.socket.send(msg);
        }
    };

    WebSocketTransport.prototype = new BaseTransport();

    WebSocketTransport.prototype.init = function (ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        var host = function (endpoint, clientId) {
            var path = window.location.host + endpoint + '/' + clientId;

            if (window.location.protocol == 'http:') {
                path = 'ws://' + path;
            } else {
                path = 'wss://' + path;
            }

            return path
        }

        this.socket = $.gracefulWebSocket(host(this.endpoint, this.ankorSystem.senderId));
        if (this.socket != null) {

            var self = this;

            this.socket.onopen = function () {
                console.log('WebSocket connected');
                self._isReady = true;
                self._sendPendingMessages();

                function heartbeat() {
                    console.log("\u2665-beat");
                    self.socket.send("");

                    // TODO: Could try to reconnect here

                    setTimeout(heartbeat, self.heartbeatInterval)
                }

                console.log("Starting heartbeat");
                setTimeout(heartbeat, self.heartbeatInterval);

                /*
                window.onbeforeunload = function() {
                    self.socket.close();
                };
                */
            };

            this.socket.onclose = function () {
                console.log('Info: WebSocket closed.');
                // TODO: Session Management: Inform user
            };

            this.socket.onmessage = function (message) {
                console.log('WebSocket received messages');
                self.receiveIncomingMessage(message.data);
            };
        }
    };

    WebSocketTransport.prototype.sendMessage = function (message) {
        BaseTransport.prototype.sendMessage.call(this, message);
        if (this._isReady) {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            this._sendMessageInner(jsonMessages.pop());
            this.outgoingMessages = [];
        }
    };

    return WebSocketTransport;
});
