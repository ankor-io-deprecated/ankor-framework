define([
    "./BaseEvent"
], function(BaseEvent) {
    var ActionEvent = function(path, eventSource, actionName, params) {
        BaseEvent.call(this, path, eventSource);

        this.actionName = actionName;
        this.params = params;
    };

    ActionEvent.prototype = new BaseEvent();

    return ActionEvent;
});