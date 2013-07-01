{Message} = require("./Message")

exports.ActionMessage = class ActionMessage extends Message
    type: "action"
    constructor: (id, name) ->
        super
        @name = name
        