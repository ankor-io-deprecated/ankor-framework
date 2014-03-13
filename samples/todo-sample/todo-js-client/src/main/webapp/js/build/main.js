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
  "build/ankorSystem",
  'base'
], function (_, React, TodoApp, ankorSystem) {
  'use strict';

  window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

  var rootRef = ankorSystem.getRef("root");

  var render = function () {
    React.renderComponent(
      TodoApp(
      {model:rootRef.appendPath("model").getValue(),
      modelRef:rootRef.appendPath("model")}
      ),
      document.getElementById('todoapp')
    );
  };
  
  // no need to render on every message
  render = _.throttle(render, 10);

  rootRef.addTreeChangeListener(render);
  rootRef.fire("init");
});

