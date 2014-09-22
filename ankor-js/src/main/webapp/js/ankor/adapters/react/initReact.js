/**
 * Tiny wrapper for React that automatically adds the AnkorMixin.
 */
define([
    './AnkorMixin'
], function (AnkorMixin) {
    return function initReact(r) {
        if (!r) {
            console.warn("React not provided.");
            r = React;
            if (!r) {
                console.error("React not found in global scope.");
                return;
            }
        }
        
        var createClass = r.createClass;
        r.createClass = function (a) {
            a.mixins = [AnkorMixin].concat(a.mixins);
            return createClass.call(r, a);
        };
        return r;
    };
});
