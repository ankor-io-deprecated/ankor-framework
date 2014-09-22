define([
    "./BaseTransport",
    "./Message"
], function (BaseTransport, Message) {
    var WebSocketTransport = function (endpointUri, options) {
        BaseTransport.call(this);

        this.options = options || {};
        this.endpointUri = endpointUri;
        this.endpoint = null;
        this.socket = null;
        this.reconnecting = false;

        this._heartbeatInterval = this.options.heartbeatInterval || 5000;
        this._heartbeatTimer = null;
        this._reconnectBackoff = this.options.reconnectBackoff || 250;
        this._reconnectDelay = this.reconnectBackoff;
        this._reconnectMaxDelay = this.options.reconnectMaxDelay || 5000;
    };

    WebSocketTransport.prototype = new BaseTransport();

    WebSocketTransport.prototype.init = function (ankorSystem) {
        BaseTransport.prototype.init.call(this, ankorSystem);

        this.endpoint = this.endpointUri + "/" + ankorSystem.senderId;
        if (this.ankorSystem.debug) {
            console.log("endpoint:", this.endpoint);
        }

        this._connect();
        window.onbeforeunload = this.utils.hitch(this, "_disconnect");
    };

    WebSocketTransport.prototype.sendMessage = function (message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        if (this.connected) {
            this._sendPendingMessages();
        }
    };

    WebSocketTransport.prototype._host = function() {
        var path = window.location.host + this.endpoint;

        if (window.location.protocol == 'http:') {
            path = 'ws://' + path;
        } else {
            path = 'wss://' + path;
        }

        return path;
    };

    WebSocketTransport.prototype._webSocket = function(host) {
        if ("WebSocket" in window) {
            return new WebSocket(host);
        }
        return null;
    };

    WebSocketTransport.prototype._connect = function() {
        this._disconnect();

        this.socket = this._webSocket(this._host(this.endpoint));

        if (this.socket) {
            this.socket.onopen = this.utils.hitch(this, "_onSocketOpen");
            this.socket.onclose = this.utils.hitch(this, "_onSocketClose");
            this.socket.onmessage = this.utils.hitch(this, "_onSocketMessage");
            this.socket.onerror = this.utils.hitch(this, "_onSocketError");
        }
    };

    WebSocketTransport.prototype._reconnect = function() {
        this._disconnect();

        if (this.reconnecting) {
            this._reconnectDelay = Math.min(this._reconnectDelay + this._reconnectBackoff, this._reconnectMaxDelay);
        }
        else {
            this.reconnecting = true;
            this._reconnectDelay = this._reconnectBackoff;
        }

        setTimeout(this.utils.hitch(this, function() {
            if (this.ankorSystem.debug) {
                console.log("WebSocket trying to reconnect (Delay = " + this._reconnectDelay + ")");
            }
            this._connect();
        }), this._reconnectDelay);
    };

    WebSocketTransport.prototype._disconnect = function() {
        this._stopHeartbeat();
        this.connected = false;
        if (this.onConnectionChange) this.onConnectionChange(this.connected);

        if (this.socket) {
            this.socket.onopen = null;
            this.socket.onclose = null;
            this.socket.onmessage = null;
            this.socket.onerror = null;
            this.socket.close();
            this.socket = null;
        }
    };

    WebSocketTransport.prototype._onSocketOpen = function() {
        this.connected = true;
        this.reconnecting = false;
        this._startHeartbeat();
        if (this.onConnectionChange) this.onConnectionChange(this.connected);

        var connectMsg = new Message({
            path: this.options.connectProperty
        });
        connectMsg.connectParams = this.options.connectParams;
        this._sendMessage(connectMsg);
        this._sendPendingMessages();
    };

    WebSocketTransport.prototype._onSocketClose = function() {
        this._reconnect();
    };

    WebSocketTransport.prototype._onSocketMessage = function(msg) {
        var message = this.decodeMessage(this.utils.jsonParse(msg.data));
        this.receiveMessage(message);
    };

    WebSocketTransport.prototype._onSocketError = function(err) {
        if (this.ankorSystem.debug) {
            console.log("WebSocketTransport onError", err);
        }

        this._reconnect();
    };

    WebSocketTransport.prototype._sendPendingMessages = function() {
        if (this.connected) {
            for (var i = 0, message; (message = this.outgoingMessages[i]); i++) {
                this._sendMessage(message);
            }
            this.outgoingMessages = [];
        }
    };

    WebSocketTransport.prototype._sendMessage = function(message) {
        var jsonMessage = this.utils.jsonStringify(this.encodeMessage(message));
        this.socket.send(jsonMessage);
    };

    WebSocketTransport.prototype._startHeartbeat = function() {
        if (!this._heartbeatTimer) {
            this._heartbeatTimer = setTimeout(this.utils.hitch(this, "_onHeartbeat"), this._heartbeatInterval);
        }
    };

    WebSocketTransport.prototype._stopHeartbeat = function() {
        if (this._heartbeatTimer) {
            clearTimeout(this._heartbeatTimer);
            this._heartbeatTimer = null;
        }
    };

    WebSocketTransport.prototype._onHeartbeat = function() {
        if (this.socket) {
            if (this.ankorSystem.debug) {
                console.log("\u2665-beat");
            }
            this.socket.send("");
            this._heartbeatTimer = setTimeout(this.utils.hitch(this, "_onHeartbeat"), this._heartbeatInterval);
        }
    };

    return WebSocketTransport;
});
