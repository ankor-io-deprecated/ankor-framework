/**
 * This is the API endpoint of the Ankor JavaScript library.
 * 
 * It exposes a namespace object containing all Ankor classes. (TODO: Remove internal classes).
 * The structure of the object is equal of the folder structure of the ankor-js source.
 * 
 * This file is used as an entry point by the r.js optimizer to generate the distribution files (bower).
 * See `Makefile` and `build.js` for more details.
 * 
 * If you add classes to ankor-js that should be available to Ankor users you have to add them here,
 * otherwise they will be missing from the distribution files.
 * 
 * This version does not contain any library specific utils or adapters.
 */
define([
    './events/ActionEvent',
    './events/BaseEvent',
    './events/ChangeEvent',
    './transport/BaseTransport',
    './transport/HttpPollingTransport',
    './transport/Message',
    './transport/WebSocketTransport',
    './utils/BaseUtils',
    './AnkorSystem',
    './BigCacheController',
    './BigList',
    './BigMap',
    './ListenerRegistry',
    './Model',
    './ModelInterface',
    './Path',
    './PathSegment',
    './Ref'
], function (ActionEvent,
             BaseEvent,
             ChangeEvent, 
             BaseTransport, 
             HttpPollingTransport, 
             Message, 
             WebSocketTransport, 
             BaseUtils,
             AnkorSystem, 
             BigCacheController, 
             BigList,
             BigMap, 
             ListenerRegistry, 
             Model, 
             ModelInterface, 
             Path, 
             PathSegment, 
             Ref) {
    
    var ankor = {
        adapters: {},
        events: {
            ActionEvent: ActionEvent,
            BaseEvent: BaseEvent,
            ChangeEvent: ChangeEvent
        },
        transport: {
            BaseTransport: BaseTransport,
            HttpPollingTransport: HttpPollingTransport, 
            Message: Message, 
            WebSocketTransport: WebSocketTransport
        },
        utils: {
            BaseUtils: BaseUtils
        },
        AnkorSystem: AnkorSystem,
        BigCacheController: BigCacheController,
        BigList: BigList,
        BigMap: BigMap,
        ListenerRegistry: ListenerRegistry, 
        Model: Model, 
        ModelInterface: ModelInterface, 
        Path: Path, 
        PathSegment: PathSegment, 
        Ref: Ref        
    };
    
    return ankor;
});
