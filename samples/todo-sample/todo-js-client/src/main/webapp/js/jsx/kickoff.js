/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */

define([
  "react",
  "director",
  "todoFooter",
  "todoItem",
  "todoApp",
  "ankorSystem",
  'base'
], function (React, Router, TodoFooter, TodoItem, TodoApp, ankorSystem) {
  'use strict';
  
  window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

  var rootRef = ankorSystem.getRef("root");

  Date.now || (Date.now = function() { return +new Date });
  
  var throttle = function(func, wait, options) {
    var context, args, result;
    var timeout = null;
    var previous = 0;
    options || (options = {});
    var later = function() {
      previous = options.leading === false ? 0 : Date.now();
      timeout = null;
      result = func.apply(context, args);
      context = args = null;
    };
    return function() {
      var now = Date.now();
      if (!previous && options.leading === false) previous = now;
      var remaining = wait - (now - previous);
      context = this;
      args = arguments;
      if (remaining <= 0) {
        clearTimeout(timeout);
        timeout = null;
        previous = now;
        result = func.apply(context, args);
        context = args = null;
      } else if (!timeout && options.trailing !== false) {
        timeout = setTimeout(later, remaining);
      }
      return result;
    };
  };
  
  var render = function() {
    React.renderComponent(
      <TodoApp
      model={rootRef.appendPath("model").getValue()}
      modelRef={rootRef.appendPath("model")}
      />,
      document.getElementById('todoapp')
    );
  };
  render = throttle(render, 10);
  
  rootRef.addTreeChangeListener(render);
  rootRef.fire("init");
});

