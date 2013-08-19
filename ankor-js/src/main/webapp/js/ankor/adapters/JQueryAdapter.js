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
});
