{Message} = require("./Message")

exports.ChangeMessage = class ChangeMessage extends Message
    type: "change"
    constructor: (id, ref, value) ->
        super
        @ref = ref
        @value = value
        