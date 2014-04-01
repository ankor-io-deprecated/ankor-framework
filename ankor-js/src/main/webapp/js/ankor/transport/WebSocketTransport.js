define([
    "./BaseTransport"
], function (BaseTransport) {
    var WebSocketTransport = function (endpointUri, options) {
        BaseTransport.call(this);

        this.options = options || {};
        var clientId = Math.random().toString(36).substring(2, 15) ||
            Math.random().toString(36).substring(2, 15);  // TODO refactor uuid
        this.endpoint = endpointUri + "/" + clientId;
        console.log("endpoint:", this.endpoint);
        this.heartbeatInterval = options.heartbeatInterval || 5000;

        this._isReady = false;

        // Sends pending messages after the connection has been established
        this._sendPendingMessages = function () {
            for (var i = 0, message; (message = this.outgoingMessages[i]); i++) {
                this._sendMessageInner(message);
            }
            this.outgoingMessages = [];
        };

         //Private method to prevent code duplication.
        this._sendMessageInner = function (message) {
            var jsonMessage = this.utils.jsonStringify(this.encodeMessage(message));
            // console.log('WebSocket send message ', jsonMessage);
            this.socket.send(jsonMessage);
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

            return path;
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
                // console.log('WebSocket connected');
                self._isReady = true;

                var ConnectMsg = function(property) {  // TODO create connectionManager, then move connect message, implement disconnect
                    this.event = {};
                    this.event["path"] = self.options.connectProperty;
                    this.connectParams = self.options.connectParams;
                };
                self.sendMessage(new ConnectMsg("root"));

                var heartbeat = function() {
                    console.log("\u2665-beat");
                    self.socket.send("");

                    setTimeout(heartbeat, self.heartbeatInterval)
                };

                // console.log("Starting heartbeat");
                heartbeat();

                // close the connection before closing the tab/browser.
                // if this doesn't fire, the dead client will be detected by the timeout
                window.onbeforeunload = function() {
                    self.socket.close();
                };
            };

            this.socket.onclose = function () {
                // console.log('Info: WebSocket closed.');
            };

            this.socket.onmessage = function (jsonMessage) {
                // the server assigns an id to this client, which will be the first message the server sends
                var message = self.decodeMessage(self.utils.jsonParse(jsonMessage.data));
                // console.log('WebSocket received messages', jsonMessage.data);
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
