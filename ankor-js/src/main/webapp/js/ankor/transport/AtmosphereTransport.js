define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
    "atmosphere"
], function(BaseTransport, ActionMessage, ChangeMessage, atmosphere) {
    var AtmosphereTransport = function(endpoint, options) {
        BaseTransport.call(this);
        this.endpoint = endpoint;
    };

    AtmosphereTransport.prototype = new BaseTransport();

    AtmosphereTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        var self = this;

        this.isReady = false;

        // We are now ready to cut the request
        var request = {
            url: this.endpoint,
            contentType : "application/json",
            logLevel : 'debug',
            transport : 'long-polling',
            fallbackTransport: 'jsonp'
            // enableProtocol: true,
            // trackMessageLength: true
        };

        request.onOpen = function(response) {
            console.log('Atmosphere connected using ', response.transport);

            // Forcing ankor to use the same uuid as atmosphere for this client
            self.ankorSystem.senderId = self.socket.getUUID();

            // Update pending messages
            for (var i = 0, message; (message = self.outgoingMessages[i]); i++) {
                message.senderId = self.ankorSystem.senderId;
            }

            self.isReady = true;
        };


        request.onMessage = function (response) {

            var doMessage = function(msg) {
                try {
                    // msg = msg.split('|')[1];
                    var message = self.utils.jsonParse(msg);
                } catch (e) {
                    if (self.ankorSystem.debug) {
                        console.log("Ankor Transport Error", e);
                    }
                    return;
                }

                if (message.change != undefined) {
                    var messageObject = new ChangeMessage(message.senderId, message.modelId, message.messageId, message.property, message.change.type, message.change.key, message.change.value);
                    self.processIncomingMessage(messageObject);
                }
            }

            var msgs = response.responseBody.match(/[^\r\n]+/g);
            for (var i = 0, msg; (msg = msgs[i]); i++) {
                doMessage(msg);
            }
        };

        request.onReopen = function(response) {
            console.log('Atmosphere re-connected using ', response.transport);
        };

        request.onClose = function(response) {
            console.log("Client closed the connection after a timeout");
        };

        request.onError = function(response) {
            console.log("Sorry, but there's some problem with your socket or the server is down");
        };

        request.onReconnect = function(request, response) {
            console.log("Connection lost, trying to reconnect. Trying to reconnect ", request.reconnectInterval);
        };

        this.socket = atmosphere.subscribe(request);
    };

    AtmosphereTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        var self = this;

        // Ankor has to wait until atmosphere establishes a connection before sending the "init" Action.
        var pushOrWait = function() {
            if (self.isReady) {
                var jsonMessages = BaseTransport.buildJsonMessages(self.outgoingMessages);
                self.outgoingMessages = [];

                while (jsonMessages.length > 0) {
                    var message = self.utils.jsonStringify(jsonMessages.pop())
                    self.socket.push(message);
                }
            } else {
                setTimeout(pushOrWait, 100);
            }
        }

        pushOrWait();
    };

    return AtmosphereTransport;
});
