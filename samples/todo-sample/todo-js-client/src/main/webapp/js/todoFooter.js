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
          React.DOM.button(
          {id:"clear-completed",
          onClick:this.props.onClearCompleted}, 
          "Clear completed (",this.props.completedCount,")"
          )
          );
      }

      // React idiom for shortcutting to `classSet` since it'll be used often
      var cx = React.addons.classSet;
      var filter = this.props.filter;
      var filterAll = cx({selected: filter === app.ALL});
      var filterActive = cx({selected: filter === app.ACTIVE});
      var filterCompleted = cx({selected: filter === app.COMPLETED});
      return (
        React.DOM.footer( {id:"footer"}, 
          React.DOM.span( {id:"todo-count"}, 
            React.DOM.strong(null, this.props.count), " ", activeTodoWord, " left"
          ),
          React.DOM.ul( {id:"filters"}, 
            React.DOM.li(null, 
              React.DOM.a( {href:"#/", className:filterAll}, 
              "All"
              )
            ),
						' ',
            React.DOM.li(null, 
              React.DOM.a( {href:"#/active", className:filterActive}, 
              "Active"
              )
            ),
						' ',
            React.DOM.li(null, 
              React.DOM.a( {href:"#/completed", className:filterCompleted}, 
              "Completed"
              )
            )
          ),
					clearButton
        )
        );
    }
  });
});
