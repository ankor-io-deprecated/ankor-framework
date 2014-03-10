define([
  "ankor/AnkorSystem",
  "ankor/transport/WebSocketTransport",
  "ankor/utils/BaseUtils",
], function (AnkorSystem, WebSocketTransport, BaseUtils) {
  return new AnkorSystem({
    debug: true,
    senderId: null,
    modelId: "collabTest",
    transport: new WebSocketTransport(),
    utils: new BaseUtils()
  });
});
  
