/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */

define([
    "underscore",
    "react",
    "build/todoApp",
    "ankor/AnkorSystem",
    "ankor/transport/WebSocketTransport",
    "ankor/utils/BaseUtils",
    'base'
], function (_, React, TodoApp, AnkorSystem, WebSocketTransport, BaseUtils) {
    'use strict';

    var render = function () {
        React.renderComponent(
            TodoApp({
            model: rootRef.appendPath("model").getValue(), 
            modelRef: rootRef.appendPath("model")}
            ),
            document.getElementById('todoapp')
        );
    };

    // no need to render on every message
    render = _.throttle(render, 10);

    var ankorSystem = new AnkorSystem({
        debug: true,
        senderId: null,
        modelId: "root",
        utils: new BaseUtils(),
        transport: new WebSocketTransport("/websocket/ankor", {
            connectProperty: "root",
            connectParams: {
                "at.irian.ankor.MODEL_INSTANCE_ID": "collaborationTest"
            }
        })
    });

    var rootRef = ankorSystem.getRef("root");

    // this is where the magic happens
    rootRef.addTreeChangeListener(render);

    ankorSystem.transport.onConnectionChange = function (connected) {
        var style = {
            color: connected ? "green" : "red"
        };
        var label = connected ? "ok" : "closed";

        React.renderComponent(
            React.DOM.p({style: style}, "Server Connection: ", label),
            document.getElementById('connectionstatus')
        )
    };
});

