/**
 * See `ankor.js`
 */
define([
    './adapters/JQueryAdapter',
    './utils/JQueryUtils',
    './ankor'
], function (JQueryAdapter, JQueryUtils, ankor) {
    ankor.adapters.jQuery.init = JQueryAdapter;
    return ankor;
});
