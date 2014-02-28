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
  'base'
], function (React, Router, TodoFooter, TodoItem) {
  'use strict';

	var ENTER_KEY = 13;

	return React.createClass({
		componentDidMount: function () {
			var setFilter = function(value) {
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
        var val = this.refs.newField.getDOMNode().value.trim();
        if (val) {
          this.props.modelRef.fire("newTask", {title: val});
          this.refs.newField.getDOMNode().value = '';
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
          TodoItem(
          {key:todo.id,
          todo:todo,
          modelRef:tasksRef.appendIndex(i),
          onDestroy:this.destroy.bind(this, i)}
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
        
				main = (
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
