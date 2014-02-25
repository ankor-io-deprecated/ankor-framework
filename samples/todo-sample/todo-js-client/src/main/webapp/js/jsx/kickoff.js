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
  "filter",
  "ankorSystem",
  "todoModel",
  'base'
], function (React, Router, TodoFooter, TodoItem, TodoApp, app, ankorSystem, TodoModel) {
  'use strict';
  
  window.ankorSystem = ankorSystem; //Make reference to ankor system globally available -> for debugging only

  var rootRef = ankorSystem.getRef("root");
  
  rootRef.addPropChangeListener(function () {
    var model = new TodoModel('react-todos');

    function render() {
      React.renderComponent(
        <TodoApp 
          model={model}
          modelRef={rootRef.appendPath("model")}
        />,
        document.getElementById('todoapp')
      );
    }

    model.subscribe(render);
    render();
  });
  
  rootRef.fire("init");
});

