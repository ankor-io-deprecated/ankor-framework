/**
 * See `ankor.js`
 */
define([
    './adapters/JQueryAdapter',
    './utils/JQueryUtils',
    './ankor'
], function (JQueryAdapter, JQueryUtils, ankor) {
    ankor.adapters.JQueryAdapter = JQueryAdapter;
    ankor.utils.JQueryUtils = JQueryUtils;
    return ankor;
});
