/**
 * Tiny wrapper for React that automatically adds the AnkorMixin.
 */
define([
    './AnkorMixin',
    'react'
], function (AnkorMixin, React) {
    var createClass = React.createClass;
    React.createClass = function (a) {
        a.mixins = [AnkorMixin].concat(a.mixins);
        return createClass.call(React, a);
    };
    return React;
});
