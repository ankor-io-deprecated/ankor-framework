define([
    "./BaseTransport"
], function (BaseTransport) {
    var WebSocketTransport = function (endpoint, options) {
        BaseTransport.call(this);

        var _options = options || {};
        this.endpoint = endpoint || "/websocket/ankor";
        this.heartbeatInterval = _options.heartbeatInterval || 5000;

        this._idReceived = false;
        this._isReady = false;

        // Sends pending messages after the connection has been established
        // will assign the server created id to the pending messages
        this._sendPendingMessages = function () {
/*<<<<<<< HEAD
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            while (jsonMessages.length > 0) {
                var jsonMessage = jsonMessages.pop();
                jsonMessage.senderId = this.ankorSystem.senderId;
                this._sendMessageInner(jsonMessage);
=======*/
            for (var i = 0, message; (message = this.outgoingMessages[i]); i++) {
                this._sendMessageInner(message);
//>>>>>>> master
            }
            this.outgoingMessages = [];
        };

/*<<<<<<< HEAD
        // Private method to prevent code duplication
        this._sendMessageInner = function (jsonMessage) {
            var msg = this.utils.jsonStringify(jsonMessage);
            console.log('WebSocket send message ', msg);
            this.socket.send(msg);
=======*/
        /**
         * Private method to prevent code duplication.
         */
        this._sendMessageInner = function (message) {
            var jsonMessage = this.utils.jsonStringify(this.encodeMessage(message));
            console.log('WebSocket send message ', jsonMessage);
            this.socket.send(jsonMessage);
//>>>>>>> master
        }
    };

    WebSocketTransport.prototype = new BaseTransport();

    WebSocketTransport.prototype.init = function (ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        var host = function (endpoint) {
            var path = window.location.host + endpoint;

            if (window.location.protocol == 'http:') {
                path = 'ws://' + path;
            } else {
                path = 'wss://' + path;
            }

            return path
        };

        var webSocket = function (host) {
            if ("WebSocket" in window) {
                return new WebSocket(host);
            }
            return null;
        };

        this.socket = webSocket(host(this.endpoint));
        if (this.socket != null) {

            var self = this;

            this.socket.onopen = function () {
                console.log('WebSocket connected');
                self._isReady = true;

                var heartbeat = function() {
                    console.log("\u2665-beat");
                    self.socket.send("");

                    setTimeout(heartbeat, self.heartbeatInterval)
                };

                console.log("Starting heartbeat");
                heartbeat();

                // close the connection before closing the tab/browser.
                // if this doesn't fire, the dead client will be detected by the timeout
                window.onbeforeunload = function() {
                    self.socket.close();
                };
            };

            this.socket.onclose = function () {
                console.log('Info: WebSocket closed.');
            };

/*<<<<<<< HEAD
            this.socket.onmessage = function (message) {
                console.log('WebSocket received messages');

                // the server assigns an id to this client, which will be the first message the server sends
                if (self._idReceived == false) {
                    console.log('WebSocket received id from server');

                    // the id will be "forced" on this ankor system
                    self.ankorSystem.senderId = message.data;
                    self._sendPendingMessages();
                    self._idReceived = true;
                } else {
                    self.receiveIncomingMessage(message.data);
                }
=======*/
            this.socket.onmessage = function (jsonMessage) {
                var message = self.decodeMessage(self.utils.jsonParse(jsonMessage.data));
                console.log('WebSocket received messages', jsonMessage.data);
                self.receiveMessage(message);
//>>>>>>> master
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
