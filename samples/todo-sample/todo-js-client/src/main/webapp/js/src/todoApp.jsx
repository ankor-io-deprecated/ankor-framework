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
      var tasks = this.props.model.tasks;

      if (tasks != null) {
        var todoItems = tasks.map(function (todo, i) {
          return (
            <TodoItem
            key={todo.id}
            todo={todo}
            modelRef={tasksRef.appendIndex(i)}
            onDestroy={this.destroy.bind(this, i)}
            />
            );
        }, this);
      }

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

        main =
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
          </section>;
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
