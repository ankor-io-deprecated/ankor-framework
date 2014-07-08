define([
    "director",
    "build/filter"
], function (Router, FILTER) {
    'use strict';

    return function (modelRef) {
        var setFilter = function (value) {
            modelRef.appendPath("filter").setValue(value);
        };
        var router = Router({
            '/': setFilter.bind(this, FILTER.ALL),
            '/active': setFilter.bind(this, FILTER.ACTIVE),
            '/completed': setFilter.bind(this, FILTER.COMPLETED)
        });
        router.init('/');
        return router;
    }
});