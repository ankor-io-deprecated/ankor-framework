define([
    "dojo/_base/declare",
    "./Message"
], function(declare, Message) {
    return declare(Message, {
        type: "action",
        constructor: function(id, name) {
            this.name = name;
        }
    })
})
