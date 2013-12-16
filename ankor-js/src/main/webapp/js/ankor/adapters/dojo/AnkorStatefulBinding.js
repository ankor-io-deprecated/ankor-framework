define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "./BaseConverter"
], function(declare, lang, BaseConverter) {
    return declare(null, {
        floodDelay: -1,
        converter: new BaseConverter(),

        constructor: function(stateful, attribute, ref, options) {
            //General members
            this.stateful = stateful;
            this.attribute = attribute;
            this.ref = ref;
            lang.mixin(this, options);

            //Internal state
            this.ignoreNextWatchEvent = false;
            this.floodTimer = null;

            //Handles
            this.watchHandle = stateful.watch(attribute, lang.hitch(this, "onStatefulChange"));
            this.ankorHandle = ref.addPropChangeListener(lang.hitch(this, "onAnkorChange"));

            //init
            this.onAnkorChange();
        },
        onStatefulChange: function(attribute, oldValue, newValue) {
            if (this.ignoreNextWatchEvent) {
                this.ignoreNextWatchEvent = false;
            }
            else {
                if (this.floodDelay == -1) {
                    this.converter.toAnkor(this.ref, this.stateful.get(this.attribute), this);
                }
                else {
                    if (this.floodTimer) {
                        clearTimeout(this.floodTimer);
                    }
                    this.floodTimer = setTimeout(lang.hitch(this, function() {
                        this.floodTimer = null;
                        this.converter.toAnkor(this.ref, this.stateful.get(this.attribute), this);
                    }), this.floodDelay);
                }
            }
        },
        onAnkorChange: function(ref, event) {
            //Ignore events that where caused by this binding
            if (event && event.eventSource == this) {
                return;
            }

            var value = this.converter.fromAnkor(this.ref);
            if (value === this.stateful.get(this.attribute)) {
                return;
            }

            this.ignoreNextWatchEvent = true;
            this.stateful.set(this.attribute, value);
        },
        remove: function() {
            this.watchHandle.remove();
            this.ankorHandle.remove();
        },
        cancel: function() {
            this.remove();
        }
    });
});
