define([], function() {
    var bindStatefulAttribute = function(stateful, attribute, ref) {
        var ignoreNextWatchEvent = false;
        var updateStateful = function(event) {
            if (event && event.eventSource == stateful) {
                return;
            }
            var value = ref.getValue();
            if (value === stateful.get(attribute)) {
                return;
            }
            ignoreNextWatchEvent = true;
            stateful.set(attribute, value);
        };
        var propChangeListener = ref.addPropChangeListener(function(ref, event) {
            updateStateful(event);
        });
        var watchListener = stateful.watch(attribute, function() {
            if (ignoreNextWatchEvent) {
                ignoreNextWatchEvent = false;
            }
            else {
                ref.setValue(stateful.get(attribute), stateful);
            }
        });

        updateStateful();

        return {
            remove: function() {
                propChangeListener.remove();
                watchListener.remove();
            }
        }
    };

    return {
        bindStatefulAttribute: bindStatefulAttribute
    };
});
