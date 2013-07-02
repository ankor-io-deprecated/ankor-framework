define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "./model/ModelRef"
], function(declare, lang, ModelRef) {
    return declare(null, {
        constructor: function() {
            this.listenerId = 0;
            this.actionListeners = {};
            this.changeListeners = {};
        },
        registerChangeListener: function(ref, listener) {
            var listenerId = this.listenerId++;
            var path = ref.getPath();
            if (!(path in this.changeListeners)) {
                this.changeListeners[path] = {};
            }
            this.changeListeners[path][listenerId] = listener;
            return {
                ref: ref,
                listenerId: listenerId,
                remove: lang.hitch(this, function() {
                    delete this.changeListeners[path][listenerId]
                })
            }
        },
        triggerChangeEvent: function(ref, oldValue, newValue) {
            setTimeout(lang.hitch(this, function() {
                var listeners = [];
                for (listenerPath in this.changeListeners) {
                    var listenerRef = new ModelRef(listenerPath);
                    if (listenerRef.matches(ref)) {
                        for (listenerId in this.changeListeners[listenerPath]) {
                            listeners.push(this.changeListeners[listenerPath][listenerId]);
                        }
                    }
                }
                for (var i = 0, listener; (listener = listeners[i]); i++) {
                    listener(ref, oldValue, newValue);
                }
            }), 0);
        }
    });
});
