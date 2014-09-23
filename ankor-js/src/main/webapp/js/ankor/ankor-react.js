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
    ankor.adapters.react = {};
    ankor.adapters.react.init = initReact;
    ankor.adapters.react.AnkorMixin = AnkorMixin;
    return ankor;
});

