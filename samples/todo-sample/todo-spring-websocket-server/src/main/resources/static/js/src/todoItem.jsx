/**
 * @jsx React.DOM
 */
/*jshint quotmark: false */
/*jshint white: false */
/*jshint trailing: false */
/*jshint newcap: false */

define([
    "react",
    "build/keys"
], function (React, KEYS) {
    'use strict';

    return React.createClass({
        setEditing: function (value) {
            this.props.modelRef.root().appendPath("model.editing").setValue(value);
            this.lastEdited = true;
        },

        handleKeyDown: function (event) {
            if (event.which === KEYS.ESCAPE_KEY) {
                this.setEditing(-1);
            } else if (event.which === KEYS.ENTER_KEY) {
                this.setEditing(-1);
            }
        },

        handleChange: function (event) {
            this.props.modelRef.appendPath("title").setValue(event.target.value);
        },

        onToggle: function () {
            var ref = this.props.modelRef.appendPath("completed");
            ref.setValue(!this.props.todo.completed);
        },

        componentDidUpdate: function () {
            if (this.props.editing && this.lastEdited) {
                this.lastEdited = false;
                var node = this.refs.editField.getDOMNode();
                node.focus();
                node.setSelectionRange(0, node.value.length);
            }
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
                        className = "toggle"
                        type = "checkbox"
                        checked = {this.props.todo.completed}
                        onChange = {this.onToggle}
                        />
                        <label onDoubleClick={this.setEditing.bind(this, this.props.index)}>
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
                    onBlur = {this.setEditing.bind(this, -1)}
                    onChange = {this.handleChange}
                    onKeyDown = {this.handleKeyDown}
                    />
                </li>
                );
        }
    });
});