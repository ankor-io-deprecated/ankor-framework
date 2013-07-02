define([
    "dojo/_base/declare",
    "./Message"
], function(declare, Message) {
    return declare(Message, {
        type: "change",
        constructor: function(id, ref, value) {
            this.ref = ref;
            this.value = value;
        }
    });
});
