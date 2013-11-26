define(function() {
    var BaseEvent = function(path, eventSource) {
        this.path = path;
        this.eventSource = eventSource;
    };

    return BaseEvent;
});