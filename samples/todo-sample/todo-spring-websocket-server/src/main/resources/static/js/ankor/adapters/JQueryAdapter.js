define([
    "jquery"
], function(jquery) {
    jquery.fn.ankorBindInnerHTML = function(ref) {
        //Helper variables and functions
        var element = this.first();
        var updateInnerHTML = function() {
            var value = ref.getValue();
            if (value === undefined || value === null) {
                value = "";
            }
            element.html(value);
        };

        //Init current value
        updateInnerHTML();

        //Change listeners
        return ref.addPropChangeListener(function() {
            updateInnerHTML();
        });
    };
    jquery.fn.ankorBindInputValue = function(ref) {
        //Helper variables and functions
        var element = this.first();
        var lastSentValue = null;
        var updateRefValue = function() {
            if (element.val() != lastSentValue) {
                lastSentValue = element.val();
                ref.setValue(element.val(), element); //Use element as eventSource
            }
        };
        var updateInputValue = function(event) {
            //Using continuation for setting value so that e.g. ankorBindSelectItems has the chance to create the select options first, before the value is then selected
            setTimeout(function() {
                //Check if eventSource is own element
                if (event && event.eventSource == element) {
                    return;
                }
                var value = ref.getValue();
                if (value === undefined || value === null) {
                    value = "";
                }
                element.val(value);
            }, 0);
        };

        //Init current value
        updateInputValue();

        //Change listeners
        //Listen for onChange
        element.change(updateRefValue);
        //Listen for onKeyDown (with flood control timer)
        var keyTimer = null;
        element.keydown(function() {
            if (keyTimer) {
                clearTimeout(keyTimer);
                keyTimer = null;
            }
            keyTimer = setTimeout(function() {
                keyTimer = null;
                updateRefValue();
            }, 200);
        });
        //List for ankor prop changes
        return ref.addPropChangeListener(function(ref, event) {
            updateInputValue(event);
        });
    };
    jquery.fn.ankorBindSelectItems = function(ref, options) {
        //Helper variables and functions
        var element = this.first();
        var select = element.get(0);
        var emptyOption = false;
        if (options && options.emptyOption != undefined) {
            emptyOption = options.emptyOption;
        }
        var updateSelectOptions = function() {
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
            for (var i = 0, selectItem; (selectItem = selectItems[i]) !== undefined; i++) {
                if (selectItem === null) {
                    selectItem = "";
                }
                var option = document.createElement("option");
                option.text = selectItem;
                option.value = selectItem;
                select.add(option);
            }
        };

        //Init current value
        updateSelectOptions();

        //Change listeners
        return ref.addPropChangeListener(function() {
            updateSelectOptions();
        });
    };

    var stringMapId = 0;
    var stringMapBuffer = {};
    $.ankorStringMap = function(attribute, ref) {
        //Get or create stringMapBuffer for this attribute/ref combination
        var stringMapKey = ref.path.toString() + "@" + attribute;
        if (!(stringMapKey in stringMapBuffer)) {
            stringMapBuffer[stringMapKey] = {};
        }
        var buffer = stringMapBuffer[stringMapKey];

        //Create list of existing bound elements
        var key;
        var toCleanup = {};
        for (key in buffer) {
            if (!buffer.hasOwnProperty(key)) {
                continue;
            }
            toCleanup[key] = buffer[key];
        }

        //Step through all found dom elements
        $("[" + attribute + "]").each(function(index, element) {
            //Only set up if there's no ankorStringMapId assigned yet
            if (!$(element).data("ankorStringMapId")) {
                var el = $(element);
                var elementId = "#" + stringMapId++;
                var stringKey = el.attr(attribute);
                var stringRef = ref.append(stringKey);
                var updateElement = function() {
                    element.innerHTML = stringRef.getValue() || "";
                };
                var listener = stringRef.addPropChangeListener(updateElement);

                el.data("ankorStringMapId", elementId);
                buffer[elementId] = {
                    element: element,
                    listener: listener,
                    updateFn: updateElement
                };
                updateElement();
            }
            //Otherwise remove from potential cleanup list
            else {
                delete toCleanup[$(element).data("ankorStringMapId")];
            }
        });

        //Clean up no longer used bindings...
        for (key in toCleanup) {
            if (!toCleanup.hasOwnProperty(key)) {
                continue;
            }
            toCleanup[key].listener.remove();
            delete buffer[key];
        }
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
