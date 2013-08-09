define([
    "./BaseTransport",
    "../messages/ActionMessage",
    "../messages/ChangeMessage",
    "../utils"
], function(BaseTransport, ActionMessage, ChangeMessage, utils) {
    var serverSenderId = "server";
    var serverMessageCounter = 0;

    var TestTransport = function() {
        BaseTransport.call(this);
    };
    TestTransport.prototype = new BaseTransport();

    TestTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        console.log("sending", message);
        setTimeout(utils.hitch(this, "processOutgoingMessages"), 0);
    };

    TestTransport.prototype.processIncomingMessage = function(message) {
        console.log("receiving", message);
        BaseTransport.prototype.processIncomingMessage.call(this, message);
    }

    TestTransport.prototype.processOutgoingMessages = function() {
        var messages = this.outgoingMessages;
        this.outgoingMessages = [];

        for (var i = 0, message; (message = messages[i]); i++) {
            //Static response to init message
            if (message instanceof ActionMessage && message.action == "init") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root", {
                    userName: "",
                    serverStatus: "",
                    tabs: {}
                });
                this.processIncomingMessage(response);

                response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.userName", "John Doe");
                this.processIncomingMessage(response);
            }
            else if (message instanceof ActionMessage && message.action == "createAnimalDetailTab") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A2", {
                    id: "A2",
                    type: "animalDetailTab",
                    model: {
                        animal: {
                            uuid: "a47c2247-a6f8-4a89-bc97-aa36fec5e664",
                            name: "",
                            type: null,
                            family: null
                        },
                        selectItems: {
                            types: [
                                "Fish",
                                "Bird",
                                "Mammal"
                            ],
                            families: []
                        },
                        editable: true,
                        nameStatus: "ok"
                    },
                    name: "New Animal"
                });
                this.processIncomingMessage(response);
            }
            else if (message instanceof ChangeMessage && message.property == "root.tabs.A2.model.animal.name") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A2.name", "New Animal (" + message.value + ")");
                this.processIncomingMessage(response);
            }
            else if (message instanceof ChangeMessage && message.property == "root.tabs.A2.model.animal.type") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A2.model.selectItems.families", ["Balaenopteridae", "Felidae"]);
                this.processIncomingMessage(response);
            }
            else if (message instanceof ActionMessage && message.action == "save" && message.property == "root.tabs.A2.model") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.serverStatus", "Animal successfully saved");
                this.processIncomingMessage(response);
            }
            else if (message instanceof ActionMessage && message.action == "createAnimalSearchTab") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A1", {
                    id: "A1",
                    type: "animalSearchTab",
                    model: {
                        filter: {
                            name: null,
                            type: null,
                            family: null
                        },
                        selectItems: {
                            types: [
                                "Fish",
                                "Bird",
                                "Mammal"
                            ],
                            families: []
                        },
                        animals: {
                            paginator: {
                                first: 0,
                                maxResults: 5,
                                count: 0
                            },
                            rows: []
                        }
                    },
                    name: "Animal Search"
                });
                this.processIncomingMessage(response);
            }
            else if (message instanceof ChangeMessage && message.property == "root.tabs.A1.model.filter.name") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A1.name", "Animal Search (" + message.value + ")");
                this.processIncomingMessage(response);

                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A1.model.animals.rows", [
                    {
                        uuid: "a47c2247-a6f8-4a89-bc97-aa36fec5e664",
                        name: "FooBar",
                        type: "Mammal",
                        family: "Felidae"
                    },
                    {
                        uuid: "a47c2247-a6f8-4a89-bc97-aa36fec5e665",
                        name: "Stinky",
                        type: "Fish",
                        family: "Felidae"
                    }
                ]);
                this.processIncomingMessage(response);
            }
            else if (message instanceof ChangeMessage && message.property == "root.tabs.A1.model.filter.type") {
                var response = new ChangeMessage(serverSenderId, message.modelId, serverSenderId + "#" + serverMessageCounter++, "root.tabs.A1.model.selectItems.families", ["Balaenopteridae", "Felidae"]);
                this.processIncomingMessage(response);
            }
        }
    };

    return TestTransport;
});
