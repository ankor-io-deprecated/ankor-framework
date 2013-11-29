define([
    "./BaseTransport"
], function(BaseTransport) {
    var HttpPollingTransport = function(endpoint, options) {
        BaseTransport.call(this);

        this.endpoint = endpoint;
        this.inFlight = null;
        this.pollingInterval = 100;
        if (options && options.pollingInterval != undefined) {
            this.pollingInterval = options.pollingInterval;
        }
    };
    HttpPollingTransport.prototype = new BaseTransport();

    HttpPollingTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);
        this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
    };

    HttpPollingTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        if (!this.inFlight) {
            this.processOutgoingMessages();
        }
    };

    HttpPollingTransport.prototype.processOutgoingMessages = function() {
        clearTimeout(this.sendTimer);
        this.inFlight = this.outgoingMessages;
        this.outgoingMessages = [];

        //Build JSON of messages
        var jsonMessages = [];
        for (var i = 0, message; (message = this.inFlight[i]); i++) {
            jsonMessages.push(this.encodeMessage(message));
        }

        //Ajax request
        this.utils.xhrPost(this.endpoint, {
            clientId: this.ankorSystem.senderId,
            messages: this.utils.jsonStringify(jsonMessages)
        }, this.utils.hitch(this, function(err, response) {
            try {
                var parsedMessages = this.utils.jsonParse(response);
            }
            catch (e) {
                err = e;
            }
            if (err) {
                if (this.ankorSystem.debug) {
                    console.log("Ankor HttpPollingTransport Error", err);
                }
                this.outgoingMessages = this.inFlight.concat(this.outgoingMessages);
            }
            else {
                for (var i = 0, parsedMessage; (parsedMessage = parsedMessages[i]); i++) {
                    var message = this.decodeMessage(parsedMessage);
                    this.receiveMessage(message);
                }
            }
            this.inFlight = null;
            this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
        }));
    };

    return HttpPollingTransport;
});
