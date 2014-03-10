/**
 * @jsx React.DOM
 */
/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */

define([
  "react",
  "build/filter"
], function (React, Filter) {
  'use strict';

  return React.createClass({
    render: function () {
      var clearButton = null;

      if (this.props.clearButtonVisibility) {
        clearButton = (
          <button
          id="clear-completed"
          onClick={this.props.onClearCompleted}>
            {this.props.itemsCompleteText}
          </button>
          );
      }

      // React idiom for shortcutting to `classSet` since it'll be used often
      var cx = React.addons.classSet;
      var filter = this.props.filter;
      var filterAll = cx({selected: filter === Filter.ALL});
      var filterActive = cx({selected: filter === Filter.ACTIVE});
      var filterCompleted = cx({selected: filter === Filter.COMPLETED});
      return (
        <footer id="footer">
          <span id="todo-count">
            <strong>{this.props.itemsLeft}</strong> {this.props.itemsLeftText}
          </span>
          <ul id="filters">
            <li>
              <a href="#/" className={filterAll}>All</a>
            </li>
						{' '}
            <li>
              <a href="#/active" className={filterActive}>Active</a>
            </li>
						{' '}
            <li>
              <a href="#/completed" className={filterCompleted}>Completed</a>
            </li>
          </ul>
					{clearButton}
        </footer>
        );
    }
  });
});
