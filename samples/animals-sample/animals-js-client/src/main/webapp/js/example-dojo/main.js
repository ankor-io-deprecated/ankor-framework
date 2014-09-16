define([
    "ankor/AnkorSystem",
    //"ankor/transport/HttpPollingTransport",
    "ankor/transport/WebSocketTransport",
    "ankor/utils/DojoUtils",
    "./AnimalSample"
], function(AnkorSystem, WebSocketTransport, DojoUtils, AnimalSample) {
    //Setup AnkorSystem
    var ankorSystem = new AnkorSystem({
        debug: true,
        modelId: "collabTest",
        //transport: new HttpPollingTransport("/ankor"),
        transport: new WebSocketTransport("/websocket/ankor", {
            connectProperty: "root"
        }),
        utils: new DojoUtils()
    });

    //Init app
    var animalSample = new AnimalSample({
        rootRef: ankorSystem.getRef("root"),
        i18nRef: ankorSystem.getRef("root.resources")
    });
    animalSample.placeAt(document.body);
    animalSample.startup();

    //Create global references -> for debugging only
    window.ankorSystem = ankorSystem;
    window.animalSample = animalSample;
});
