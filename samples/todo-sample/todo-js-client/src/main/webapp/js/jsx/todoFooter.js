/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */

define([
  "react",
  "utils",
  "filter"
], function (React, Utils, app) {
  'use strict';

  return React.createClass({
    render: function () {
      var activeTodoWord = Utils.pluralize(this.props.count, 'item');
      var clearButton = null;

      if (this.props.completedCount > 0) {
        clearButton = (
          <button
          id="clear-completed"
          onClick={this.props.onClearCompleted}>
          Clear completed ({this.props.completedCount})
          </button>
          );
      }

      // React idiom for shortcutting to `classSet` since it'll be used often
      var cx = React.addons.classSet;
      var filter = this.props.filter;
      var filterAll = cx({selected: filter === app.ALL});
      var filterActive = cx({selected: filter === app.ACTIVE});
      var filterCompleted = cx({selected: filter === app.COMPLETED});
      return (
        <footer id="footer">
          <span id="todo-count">
            <strong>{this.props.count}</strong> {activeTodoWord} left
          </span>
          <ul id="filters">
            <li>
              <a href="#/" className={filterAll}>
              All
              </a>
            </li>
						{' '}
            <li>
              <a href="#/active" className={filterActive}>
              Active
              </a>
            </li>
						{' '}
            <li>
              <a href="#/completed" className={filterCompleted}>
              Completed
              </a>
            </li>
          </ul>
					{clearButton}
        </footer>
        );
    }
  });
});
