define([
    "dojo/_base/declare"
], function(declare) {
    var ModelObject = declare(null, {
        constructor: function(ankorSystem, ref) {
            this.ankorSystem = ankorSystem;
            this.ref = ref;
            this._properties = {};
        },
        get: function(propertyName) {
            if (!(propertyName in this._properties)) {
                throw new Error("Model has no property " + propertyName);
            }
            return this._properties[propertyName];
        },
        set: function(propertyName, value) {
            this.apply(propertyName, value);
            //Generate message
        },
        apply: function(propertyName, value) {
            var propertyRef = this.ref.getSubPropertyRef(propertyName);
            var oldValue = this._properties[propertyName];

            if (value instanceof Array) {
                throw new Error("Array not supported yet");
            }
            else if (typeof value == "object" && value != null) {
                var modelObject = new ModelObject(this.ankorSystem, propertyRef);
                for (subPropertyName in value) {
                    modelObject.apply(subPropertyName, value[subPropertyName])
                }
                value = modelObject;
            }

            this._properties[propertyName] = value;
            this.ankorSystem.listenerRegistry.triggerChangeEvent(propertyRef, oldValue, value);
        },
        toJson: function() {
            var plainObject = {};
            for (propertyName in this._properties) {
                if (this._properties[propertyName] instanceof this.constructor) {
                    plainObject[propertyName] = this._properties[propertyName].toJson();
                }
                else {
                    plainObject[propertyName] = this._properties[propertyName];
                }
            }
        }
    });
    return ModelObject;
});
