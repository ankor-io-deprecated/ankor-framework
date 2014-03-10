/**
 * @jsx React.DOM
 */
/*jshint quotmark: false */
/*jshint white: false */
/*jshint trailing: false */
/*jshint newcap: false */

define([
  "react"
], function (React) {
  'use strict';

  var ESCAPE_KEY = 27;
  var ENTER_KEY = 13;

  return React.createClass({
    handleSubmit: function () {
      var ref = this.props.modelRef.appendPath("editing");
      ref.setValue(false);
    },

    handleEdit: function () {
      this.props.modelRef.appendPath("editing").setValue(true);
    },

    handleKeyDown: function (event) {
      var ref = this.props.modelRef.appendPath("editing");
      if (event.which === ESCAPE_KEY) {
        ref.setValue(false);
      } else if (event.which === ENTER_KEY) {
        ref.setValue(false);
      }
    },

    handleChange: function (event) {
      this.props.modelRef.appendPath("title").setValue(event.target.value);
    },

    onToggle: function () {
      var ref = this.props.modelRef.appendPath("completed");
      ref.setValue(!this.props.todo.completed);
    },

    render: function () {
      var classes = React.addons.classSet({
        completed: this.props.todo.completed,
        editing: this.props.todo.editing
      });

      return (
        <li className={classes}>
          <div className="view">
            <input
            className = "toggle"
            type = "checkbox"
            checked = {this.props.todo.completed}
            onChange = {this.onToggle}
            />
            <label onDoubleClick={this.handleEdit}>
              {this.props.todo.title}
            </label>
            <button
            className = "destroy"
            onClick = {this.props.onDestroy}
            />
          </div>
          <input
          ref = "editField"
          className = "edit"
          value = {this.props.todo.title}
          onBlur = {this.handleSubmit}
          onChange = {this.handleChange}
          onKeyDown = {this.handleKeyDown}
          />
        </li>
        );
    }
  });
});