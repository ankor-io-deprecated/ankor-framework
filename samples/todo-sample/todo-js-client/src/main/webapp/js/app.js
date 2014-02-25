/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */
/*global React, Router*/

define([
  "react",
  "director",
  "footer",
  "todoModel",
  "todoItem",
  "filter",
  'base'
], function (React, Router, TodoFooter, TodoModel, TodoItem, app) {
  'use strict';

	app.ALL = 'all';
	app.ACTIVE = 'active';
	app.COMPLETED = 'completed';

	var ENTER_KEY = 13;

	var TodoApp = React.createClass({displayName: 'TodoApp',
		getInitialState: function () {
			return {
				filter: app.ALL,
				editing: null
			};
		},
    
		componentDidMount: function () {
			var setState = this.setState;
			var router = Router({
				'/': setState.bind(this, {filter: app.ALL}),
				'/active': setState.bind(this, {filter: app.ACTIVE}),
				'/completed': setState.bind(this, {filter: app.COMPLETED})
			});
			router.init('/');
		},

		handleNewTodoKeyDown: function (event) {
			if (event.which !== ENTER_KEY) {
				return;
			}

			var val = this.refs.newField.getDOMNode().value.trim();

			if (val) {
				this.props.model.addTodo(val);
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
			var todos = this.props.model.todos;

			var shownTodos = todos.filter(function (todo) {
				switch (this.state.filter) {
				case app.ACTIVE:
					return !todo.completed;
				case app.COMPLETED:
					return todo.completed;
				default:
					return true;
				}
			}, this);

			var todoItems = shownTodos.map(function (todo) {
				return (
					TodoItem(
						{key:todo.id,
						todo:todo,
						onToggle:this.toggle.bind(this, todo),
						onDestroy:this.destroy.bind(this, todo),
						onEdit:this.edit.bind(this, todo),
						editing:this.state.editing === todo.id,
						onSave:this.save.bind(this, todo),
						onCancel:this.cancel}
					)
				);
			}, this);

			var activeTodoCount = todos.reduce(function (accum, todo) {
				return todo.completed ? accum : accum + 1;
			}, 0);

			var completedCount = todos.length - activeTodoCount;

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

	var model = new TodoModel('react-todos');

	function render() {
		React.renderComponent(
			TodoApp( {model:model}),
			document.getElementById('todoapp')
		);
	}

	model.subscribe(render);
	render();
});
