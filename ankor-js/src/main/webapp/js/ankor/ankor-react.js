/**
 * See `ankor.js`
 */
define([
    './adapters/react/AnkorMixin',
    './adapters/react/initReact',
    './ankor'
], function (AnkorMixin,
             initReact,
             ankor) {
    ankor.adapters.React = {};
    ankor.adapters.React.init = initReact;
    ankor.adapters.React.AnkorMixin = AnkorMixin;
    return ankor;
});

