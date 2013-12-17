define([
    "dojo/_base/declare"
], function(declare) {
    return declare(null, {
        fromAnkor: function(ref) {
            var value = ref.getValue();
            if (!value) {
                value = "";
            }
            return value;
        },
        toAnkor: function(ref, value, eventSource) {
            ref.setValue(value, eventSource);
        }
    });
});
