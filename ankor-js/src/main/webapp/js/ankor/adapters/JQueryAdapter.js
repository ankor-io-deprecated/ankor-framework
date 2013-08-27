define([
    "jquery"
], function(jquery) {
    jquery.fn.ankorBindInnerHTML = function(ref) {
        var element = this.first();
        return ref.addPropChangeListener(function() {
            element.html(ref.getValue());
        });
    };
    jquery.fn.ankorBindInputValue = function(ref) {
        var element = this.first();
        var lastSentValue = null;
        var updateValue = function() {
            if (element.val() != lastSentValue) {
                lastSentValue = element.val();
                ref.setValue(element.val());
            }            
        };
        element.change(updateValue);
        var keyTimer = null;
        element.keydown(function() {
            if (keyTimer) {
                clearTimeout(keyTimer);
                keyTimer = null;
            }
            keyTimer = setTimeout(function() {
                keyTimer = null;
                updateValue();
            }, 200);
        });
        return ref.addPropChangeListener(function() {
            //Using continuation for setting value so that e.g. ankorBindSelectItems has the chance to create the select options first, before the value is then selected
            setTimeout(function() {
                element.val(ref.getValue());
            }, 0);
        });
    };
    jquery.fn.ankorBindSelectItems = function(ref, options) {
        var element = this.first();
        var select = element.get(0);

        var emptyOption = false;
        if (options && options.emptyOption != undefined) {
            emptyOption = options.emptyOption;
        }

        return ref.addPropChangeListener(function() {
            while (select.length > 0) {
                select.remove(0);
            }
            var selectItems = ref.getValue();
            if (emptyOption) {
                var option = document.createElement("option");
                option.text = "";
                option.value = "";
                select.add(option);
            }
            for (var i = 0, selectItem; (selectItem = selectItems[i]); i++) {
                var option = document.createElement("option");
                option.text = selectItem;
                option.value = selectItem;
                select.add(option);
            }
        });
    };

    $.fn.ankorBind1 = function(functionName, ref) {
        var element = this.first();

        return ref.addPropChangeListener(function(ref) {
            $.fn[functionName].call(element, ref.getValue());
        });
    };

    $.fn.ankorBind1Bidirectional = function(functionName, ref) {
        var element = this.first();

        element.on("change", function() {
            var value = $.fn[functionName].call(element);
            ref.setValue(value);
        });

        return ref.addPropChangeListener(function(ref) {
            $.fn[functionName].call(element, ref.getValue());
        });
    };

    $.fn.ankorBind2 = function(functionName, propertyName, ref) {
        var element = this.first();

        return ref.addPropChangeListener(function(ref) {
            $.fn[functionName].call(element, propertyName, ref.getValue());
        });
    };

    $.fn.ankorBind2Bidirectional = function(functionName, propertyName, ref) {
        var element = this.first();

        element.on("change", function() {
            var value = $.fn[functionName].call(element, propertyName);
            ref.setValue(value);
        });

        return ref.addPropChangeListener(function(ref) {
            $.fn[functionName].call(element, propertyName, ref.getValue());
        });
    };

    $.fn.ankorBindHtml = function(ref) {
        return $.fn.ankorBind1.call(this, "html", ref);
    };

    $.fn.ankorBindText = function(ref) {
        return $.fn.ankorBind1.call(this, "text", ref);
    };

    $.fn.ankorBindProp = function(propertyName, ref) {
        return $.fn.ankorBind2Bidirectional.call(this, "prop", propertyName, ref);
    };

    $.fn.ankorBindAttr = function(propertyName, ref) {
        return $.fn.ankorBind2Bidirectional.call(this, "attr", propertyName, ref);
    };

    $.fn.ankorBindData = function(propertyName, ref) {
        return $.fn.ankorBind2Bidirectional.call(this, "data", propertyName, ref);
    };

    $.fn.ankorBindToggle = function(ref) {
        return $.fn.ankorBind1.call(this, "toggle", ref);
    }

    $.fn.ankorBindToggleClass = function(clazz, ref) {
        return $.fn.ankorBind2.call(this, "toggleClass", clazz, ref);
    }
});
