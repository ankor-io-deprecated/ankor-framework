define([
    "ankor/AnkorSystem",
    "ankor/transport/WebSocketTransport",
    "ankor/utils/BaseUtils",
], function (AnkorSystem, WebSocketTransport, BaseUtils) {
    return new AnkorSystem({
        debug: true,
        senderId: null,
        modelId: "collaborationTest",
        transport: new WebSocketTransport("/stateless/websocket/ankorstateless", {"connectProperty": "root", "connectParams" : {"todoListId" : "collaborationTest"}}),
        utils: new BaseUtils()
    });
});