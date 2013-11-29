define([
    "./BaseEvent"
], function(BaseEvent) {
    var ActionEvent = function(path, eventSource, action) {
        BaseEvent.call(this, path, eventSource);

        this.action = action;
    };

    ActionEvent.prototype = new BaseEvent();

    return ActionEvent;
});