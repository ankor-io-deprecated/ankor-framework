define([
  "ankor/AnkorSystem",
  "ankor/transport/WebSocketTransport",
  "ankor/utils/BaseUtils",
  "react",
  "director",
  "base"
], function (AnkorSystem, WebSocketTransport, BaseUtils, React, Router) {
  
  //Setup AnkorSystem
  var ankorSystem = new AnkorSystem({
    debug: true,
    senderId: null,
    modelId: "collabTest",
    transport: new WebSocketTransport(),
    utils: new BaseUtils()
  });
  
  window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

  var rootRef = ankorSystem.getRef("root");
  rootRef.addPropChangeListener(function (e) {
    console.log(e);
    // new TaskList(rootRef.append("model"));
  });
  rootRef.fire("init");
});

