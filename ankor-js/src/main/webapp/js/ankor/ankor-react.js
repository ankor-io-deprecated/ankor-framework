/**
 * See `ankor.js`
 */
define([
    './adapters/react/AnkorMixin',
    './adapters/react/React',
    './ankor'
], function (AnkorMixin,
             React,
             ankor) {
    ankor.adapters.react = {};
    ankor.adapters.react.AnkorMixin = AnkorMixin;
    ankor.adapters.react.React = React;
    return ankor;
});

