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
  "todoModel",
  "todoItem",
  "filter",
  'base'
], function (React, Router, TodoFooter, TodoModel, TodoItem, app) {
  'use strict';

	var ENTER_KEY = 13;

	return React.createClass({
		getInitialState: function () {
			return {
				filter: app.ALL,
				editing: null
			};
		},
    
		componentDidMount: function () {
			var setFilter = function(path) {
        this.props.modelRef.appendPath(path).setValue(true);
      };
			var router = Router({
				'/': setFilter.bind(this, "filterAllSelected"),
				'/active': setFilter.bind(this, "filterActiveSelected"),
				'/completed': setFilter.bind(this, "filterCompleteSelected")
			});
			router.init('/');
		},

		handleNewTodoKeyDown: function (event) {
			if (event.which !== ENTER_KEY) {
				return;
			}

			var val = this.refs.newField.getDOMNode().value.trim();

			if (val) {
        this.props.modelRef.fire("newTask", {title: val});
				this.refs.newField.getDOMNode().value = '';
			}

			return false;
		},

		toggleAll: function (event) {
			var checked = event.target.checked;
			this.props.model.toggleAll(checked);
		},

		toggle: function (todoToToggle) {
			this.props.model.toggle(todoToToggle);
		},

		destroy: function (todo) {
			this.props.model.destroy(todo);
		},

		edit: function (todo, callback) {
			// refer to todoItem.js `handleEdit` for the reasoning behind the
			// callback
			this.setState({editing: todo.id}, function () {
				callback();
			});
		},

		save: function (todoToSave, text) {
			this.props.model.save(todoToSave, text);
			this.setState({editing: null});
		},

		cancel: function () {
			this.setState({editing: null});
		},

		clearCompleted: function () {
			this.props.model.clearCompleted();
		},

		render: function () {
			var footer;
			var main;
      var todosRef = this.props.modelRef.appendPath("tasks"); 
			var todos = todosRef.getValue();
      var i = 0;
      
			var todoItems = todos.map(function (todo) {
				return (
					TodoItem(
						{key:todo.id,
						todo:todo,
            modelRef:todosRef.appendIndex(i++),
						onToggle:this.toggle.bind(this, todo),
						onDestroy:this.destroy.bind(this, todo),
						onEdit:this.edit.bind(this, todo),
						editing:this.state.editing === todo.id,
						onSave:this.save.bind(this, todo),
						onCancel:this.cancel}
					)
				);
			}, this);

			var activeTodoCount = this.props.modelRef.appendPath("itemsLeft").getValue();

			var completedCount = this.props.modelRef.appendPath("itemsComplete").getValue();

			if (activeTodoCount || completedCount) {
				footer =
					TodoFooter(
						{count:activeTodoCount,
						completedCount:completedCount,
						filter:this.state.filter,
						onClearCompleted:this.clearCompleted}
					);
			}

			if (todos.length) {
				main = (
					React.DOM.section( {id:"main"}, 
						React.DOM.input(
							{id:"toggle-all",
							type:"checkbox",
							onChange:this.toggleAll,
							checked:activeTodoCount === 0}
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
