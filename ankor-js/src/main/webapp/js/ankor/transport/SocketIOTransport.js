define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
    "socketIO"
], function(BaseTransport, ActionMessage, ChangeMessage, io) {
    var SocketIOTransport = function(endpoint, options) {
        BaseTransport.call(this);
        this.endpoint = endpoint;
        this.isReady = false;

        /**
         * Sends pending messages after the connection has be established, or re-established.
         * A new connection has a new uuid, so the pending messages have to have their senderId updated.
         *
         * @param uuid The uuid of the new connection.
         */
        this.sendPendingMessages = function(uuid) {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            while (jsonMessages.length > 0) {
                var jsonMessage = jsonMessages.pop()
                jsonMessage.senderId = uuid;
                this.sendMessageInner(jsonMessage);
            }
            this.outgoingMessages = [];
        }

        /**
         * Private method to prevent code duplication.
         */
        this.sendMessageInner = function(jsonMessage) {
            var msg = this.utils.jsonStringify(jsonMessage)
            console.log('socket.io send message ', msg);
            this.socket.send(msg);
        }
    };

    SocketIOTransport.prototype = new BaseTransport();

    SocketIOTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        var self = this;

        // XXX: Expose parameter for address
        this.socket = io.connect('http://localhost:9092');

        this.socket.on('connect', function() {
            console.log('socket.io connected using ', self.socket.socket.transport);
            var uuid = self.socket.socket.sessionid;
            self.ankorSystem.senderId = uuid;
            self.sendPendingMessages(uuid);
            self.isReady = true;
        });

        this.socket.on('disconnect', function() {
            console.log('socket.io disconnected');
        });

        this.socket.on('message', function(data) {
            console.log('socket.io received messages');
            var msgs = data.match(/[^\r\n]+/g);
            for (var i = 0, msg; (msg = msgs[i]); i++) {
                self.receiveIncomingMessage(msg);
            }
        });
    };

    SocketIOTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);
        if (this.isReady) {
            var jsonMessages = BaseTransport.buildJsonMessages(this.outgoingMessages);
            this.sendMessageInner(jsonMessages.pop());
            this.outgoingMessages = [];
        }
    };

    return SocketIOTransport;
});
