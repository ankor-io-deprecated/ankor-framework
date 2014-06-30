/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */

define([
  "react",
  "build/initRouter",
  "build/todoFooter",
  "build/todoItem",
  "build/keys",
  'base'
], function (React, initRouter, TodoFooter, TodoItem, KEYS) {
  'use strict';

  return React.createClass({
    componentDidMount: function () {
      initRouter(this.props.modelRef);
    },

    handleNewTodoKeyDown: function (event) {
      if (event.which === KEYS.ENTER_KEY) {
        var node = this.refs.newField.getDOMNode();
        var val = node.value.trim();
        if (val) {
          this.props.modelRef.fire("newTask", {title: val});
          node.value = '';
        }
      }
    },

    toggleAll: function () {
      this.props.modelRef.fire("toggleAll", {toggleAll: !this.props.model.toggleAll});
    },

    destroy: function (taskId) {
      this.props.modelRef.fire("deleteTask", {id: taskId});
    },

    clearCompleted: function () {
      this.props.modelRef.fire("clearTasks");
    },

    render: function () {
      var footer;
      var main;

      var tasksRef = this.props.modelRef.appendPath("tasks");
      var tasks = this.props.model.tasks;

      var todoItems = tasks.map(function (todo, i) {
        return (
          TodoItem(
          {key:todo.id,
          todo:todo,
          modelRef:tasksRef.appendIndex(i),
          onDestroy:this.destroy.bind(this, todo.id)}
          )
          );
      }, this);

      if (this.props.model.footerVisibility) {
        footer =
          TodoFooter(
          {filter:this.props.model.filter,
          clearButtonVisibility:this.props.model.clearButtonVisibility,
          itemsLeft:this.props.model.itemsLeft,
          itemsLeftText:this.props.model.itemsLeftText,
          itemsCompleteText:this.props.model.itemsCompleteText,
          onClearCompleted:this.clearCompleted}
          );

        main =
          React.DOM.section( {id:"main"}, 
            React.DOM.input(
            {id:"toggle-all",
            type:"checkbox",
            onChange:this.toggleAll,
            checked:this.props.model.toggleAll}
            ),
            React.DOM.ul( {id:"todo-list"}, 
							todoItems
            )
          );
      }

      return (
        React.DOM.div(null, 
          React.DOM.header( {id:"header"}, 
            React.DOM.h1(null, "todos"),
            React.DOM.input(
            {ref:"newField",
            id:"new-todo",
            placeholder:"What needs to be done?",
            onKeyDown:this.handleNewTodoKeyDown,
            autoFocus:true}
            )
          ),
					main,
					footer
        )
        );
    }
  });
});
