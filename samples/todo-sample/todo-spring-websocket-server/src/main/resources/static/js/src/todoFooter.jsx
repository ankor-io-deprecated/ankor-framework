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
], function (React, FILTER) {
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

            var cx = React.addons.classSet;
            var filter = this.props.filter;
            var filterAll = cx({selected: filter === FILTER.ALL});
            var filterActive = cx({selected: filter === FILTER.ACTIVE});
            var filterCompleted = cx({selected: filter === FILTER.COMPLETED});
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
