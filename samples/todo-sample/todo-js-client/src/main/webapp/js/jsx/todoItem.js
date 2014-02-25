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

    getInitialState: function () {
      return {editText: this.props.todo.title};
    },

    /**
     * This is a completely optional performance enhancement that you can implement
     * on any React component. If you were to delete this method the app would still
     * work correctly (and still be very performant!), we just use it as an example
     * of how little code it takes to get an order of magnitude performance improvement.
     */
    /*
    shouldComponentUpdate: function (nextProps, nextState) {
      return (
        nextProps.todo !== this.props.todo ||
          nextProps.editing !== this.props.editing ||
          nextState.editText !== this.state.editText
        );
    },
    */
    
    onToggle: function() {
      var ref = this.props.modelRef;
      ref.setValue(!ref.getValue());
    },

    render: function () {
      var classes = React.addons.classSet({
        completed: this.props.todo.completed,
        editing: this.props.editing
      });
      return (
        <li className={classes}>
          <div className="view">
            <input
              className="toggle"
              type="checkbox"
              checked={this.props.todo.completed}
              onChange={this.onToggle}
            />
            <label onDoubleClick={this.handleEdit}>
              {this.props.todo.title}
            </label>
            <button className="destroy" onClick={this.props.onDestroy} />
          </div>
          <input
            ref = "editField"
            className = "edit"
            value = {this.state.editText}
            onBlur = {this.handleSubmit}
            onChange = {this.handleChange}
            onKeyDown = {this.handleKeyDown}
          />
        </li>
      );
    }
  });
});