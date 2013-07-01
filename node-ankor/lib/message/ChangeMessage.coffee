{Message} = require("./Message")

exports.ChangeMessage = class ChangeMessage extends Message
    type: "change"
    constructor: (id, path, value) ->
        super
        @path = path
        @value = value
        