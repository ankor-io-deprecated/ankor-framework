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
    "ankor",
    "build/todoApp"
], function (_, React, ankor, TodoApp) {
    'use strict';

    var render = function () {
        React.renderComponent(
            <TodoApp
            model={rootRef.appendPath("model").getValue()}
            modelRef={rootRef.appendPath("model")}
            />,
            document.getElementById('todoapp')
        );
    };

    // no need to render on every message
    render = _.throttle(render, 10);

    var ankorSystem = new ankor.AnkorSystem({
        debug: true,
        senderId: null,
        modelId: "root",
        utils: new ankor.utils.BaseUtils(),
        transport: new ankor.transport.WebSocketTransport("/websocket/ankor", {
            connectProperty: "root"
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
            <p style={style}>Server Connection: {label}</p>,
            document.getElementById('connectionstatus')
        )
    };
});

