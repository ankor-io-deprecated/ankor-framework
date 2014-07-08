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
                    React.DOM.button(
                    {id:"clear-completed",
                    onClick:this.props.onClearCompleted}, 
            this.props.itemsCompleteText
                    )
                    );
            }

            var cx = React.addons.classSet;
            var filter = this.props.filter;
            var filterAll = cx({selected: filter === FILTER.ALL});
            var filterActive = cx({selected: filter === FILTER.ACTIVE});
            var filterCompleted = cx({selected: filter === FILTER.COMPLETED});
            return (
                React.DOM.footer( {id:"footer"}, 
                    React.DOM.span( {id:"todo-count"}, 
                        React.DOM.strong(null, this.props.itemsLeft), " ", this.props.itemsLeftText
                    ),
                    React.DOM.ul( {id:"filters"}, 
                        React.DOM.li(null, 
                            React.DOM.a( {href:"#/", className:filterAll}, "All")
                        ),
						' ',
                        React.DOM.li(null, 
                            React.DOM.a( {href:"#/active", className:filterActive}, "Active")
                        ),
						' ',
                        React.DOM.li(null, 
                            React.DOM.a( {href:"#/completed", className:filterCompleted}, "Completed")
                        )
                    ),
					clearButton
                )
                );
        }
    });
});
