define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
    "atmosphere"
], function(BaseTransport, ActionMessage, ChangeMessage, atmosphere) {
    var AtmosphereTransport = function(endpoint, options) {
        BaseTransport.call(this);

        var self = this;

        this.endpoint = endpoint;
        this.isReady = false;

        // We are now ready to cut the request
        var request = {
            url: this.endpoint,
            contentType : "application/json",
            logLevel : 'debug',
            transport : 'websocket',
            fallbackTransport: 'long-polling'
        };

        request.onOpen = function(response) {
            console.log('Atmosphere connected using ', response.transport);
            self.isReady = true;
        };

        request.onMessage = function (response) {
            try {
                var messages = self.utils.jsonParse(response.responseBody);
            } catch (e) {
                if (self.ankorSystem.debug) {
                    console.log("Ankor HttpPollingTransport Error", err);
                }
                return;
            }

            for (var i = 0, message; (message = messages[i]); i++) {
                if (message.change != undefined) {
                    var messageObject = new ChangeMessage(message.senderId, message.modelId, message.messageId, message.property, message.change.type, message.change.key, message.change.value);
                    self.processIncomingMessage(messageObject);
                }
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

        this.subSocket = atmosphere.subscribe(request);
    };

    AtmosphereTransport.prototype = new BaseTransport();

    AtmosphereTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);
    };

    AtmosphereTransport.prototype.sendMessage = function(message) {
        var self = this;
        var msg = message;

        var pushOrWait = function() {
            if (self.isReady) {
                BaseTransport.prototype.sendMessage.call(self, msg);
                var jsonMessages = BaseTransport.buildJsonMessages(self.outgoingMessages);
                self.outgoingMessages = [];

                var messages = self.utils.jsonStringify(jsonMessages)
                self.subSocket.push(messages);
            } else {
                setTimeout(pushOrWait, 100);
            }
        }

        pushOrWait();
    };

    return AtmosphereTransport;
});
