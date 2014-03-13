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
  "build/todoFooter",
  "build/todoItem",
  'base'
], function (React, Router, TodoFooter, TodoItem) {
  'use strict';

  var ENTER_KEY = 13;

  return React.createClass({
    componentDidMount: function () {
      var setFilter = function (value) {
        this.props.modelRef.appendPath("filter").setValue(value);
      };
      var router = Router({
        '/': setFilter.bind(this, "all"),
        '/active': setFilter.bind(this, "active"),
        '/completed': setFilter.bind(this, "completed")
      });
      router.init('/');
    },

    handleNewTodoKeyDown: function (event) {
      if (event.which === ENTER_KEY) {
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

    destroy: function (i) {
      this.props.modelRef.fire("deleteTask", {index: i});
    },

    clearCompleted: function () {
      this.props.modelRef.fire("clearTasks");
    },

    render: function () {
      var footer;
      var main;

      var tasksRef = this.props.modelRef.appendPath("tasks");
      var task = this.props.model.tasks;

      var todoItems = task.map(function (todo, i) {
        return (
          <TodoItem
          key={todo.id}
          todo={todo}
          modelRef={tasksRef.appendIndex(i)}
          onDestroy={this.destroy.bind(this, i)}
          />
          );
      }, this);

      if (this.props.model.footerVisibility) {
        footer =
          <TodoFooter
          filter={this.props.model.filter}
          clearButtonVisibility={this.props.model.clearButtonVisibility}
          itemsLeft={this.props.model.itemsLeft}
          itemsLeftText={this.props.model.itemsLeftText}
          itemsCompleteText={this.props.model.itemsCompleteText}
          onClearCompleted={this.clearCompleted}
          />;

        main = (
          <section id="main">
            <input
            id="toggle-all"
            type="checkbox"
            onChange={this.toggleAll}
            checked={this.props.model.toggleAll}
            />
            <ul id="todo-list">
							{todoItems}
            </ul>
          </section>
          );
      }

      return (
        <div>
          <header id="header">
            <h1>todos</h1>
            <input
            ref="newField"
            id="new-todo"
            placeholder="What needs to be done?"
            onKeyDown={this.handleNewTodoKeyDown}
            autoFocus={true}
            />
          </header>
					{main}
					{footer}
        </div>
        );
    }
  });
});
