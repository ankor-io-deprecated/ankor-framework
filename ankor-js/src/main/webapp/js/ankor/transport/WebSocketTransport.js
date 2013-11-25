define([
    "jquery",
    "./BaseTransport",
    "gracefulWebSocket"             //Require only, no reference needed
], function ($, BaseTransport) {
    var WebSocketTransport = function (endpoint, options) {
        BaseTransport.call(this);

        var _options = options || {};
        this.endpoint = endpoint || "/websocket/ankor";
        this.heartbeatInterval = _options.heartbeatInterval || 25000;

        var _isReady = false;

        /**
         * Sends pending messages after the connection has be established.
         */
        this._sendPendingMessages = function () {
            for (var i = 0, message; (message = this.outgoingMessages[i]); i++) {
                this._sendMessageInner(message);
            }
            this.outgoingMessages = [];
        };

        /**
         * Private method to prevent code duplication.
         */
        this._sendMessageInner = function (message) {
            var jsonMessage = this.utils.jsonStringify(this.encodeMessage(message));
            console.log('WebSocket send message ', jsonMessage);
            this.socket.send(jsonMessage);
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
        };

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

                    setTimeout(heartbeat, self.heartbeatInterval)
                }

                console.log("Starting heartbeat");
                setTimeout(heartbeat, self.heartbeatInterval);

                window.onbeforeunload = function() {
                    self.socket.close();
                };
            };

            this.socket.onclose = function () {
                console.log('Info: WebSocket closed.');
            };

            this.socket.onmessage = function (jsonMessage) {
                var message = self.decodeMessage(self.utils.jsonParse(jsonMessage.data));
                console.log('WebSocket received messages', jsonMessage.data);
                self.receiveMessage(message);
            };
        }
    };

    WebSocketTransport.prototype.sendMessage = function (message) {
        BaseTransport.prototype.sendMessage.call(this, message);
        if (this._isReady) {
            this._sendMessageInner(message);
            this.outgoingMessages = [];
        }
    };

    return WebSocketTransport;
});
