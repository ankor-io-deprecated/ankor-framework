define([
    "jquery",
    "text!./templates/AnimalSearchTab.html"
], function($, template) {
    template = new EJS({
        text: template
    });

    var AnimalSearchTab = function(ref) {
        var tab = ref.getValue();

        //Create tab header
        $("#tabs ul").append("<li class='" + tab.id + "'><a href='#tab-" + tab.id + "'><span></span></a><a href='#' class='close'>x</a></li>");
        $("#tabs ul li." + tab.id + " span").ankorBindInnerHTML(ref.append("name"));
        $("#tabs ul li." + tab.id + " a.close").click(function() {
            ref.setValue(null);
        });

        //Create tab body
        $("#tabs").append(template.render({
            id: tab.id
        }));

        //Bind tab body fields
        var body = $("#tabs #tab-" + tab.id);
        body.find("input.name").ankorBindInputValue(ref.append("model.filter.name"));
        body.find("select.type").ankorBindSelectItems(ref.append("model.selectItems.types"));
        body.find("select.type").ankorBindInputValue(ref.append("model.filter.type"));
        body.find("select.family").ankorBindSelectItems(ref.append("model.selectItems.families"));
        body.find("select.family").ankorBindInputValue(ref.append("model.filter.family"));
        body.find("button.previous").click(function() {
            ref.append("model.animals.paginator").fire("previous");
        });
        body.find("button.next").click(function() {
            ref.append("model.animals.paginator").fire("next");
        });
        body.find("button.save").click(function() {
            ref.append("model").fire("save");
        });

        //Table rendering
        var listeners = [];
        ref.append("model.animals.rows").addPropChangeListener(function(ref) {
            var rows = ref.getValue();
            var tbody = body.find("tbody");

            //Cleanup
            tbody.html("");
            for (var i = 0, listener; (listener = listeners[i]); i++) {
                listener.remove();
            }
            listeners = [];

            //Render new table
            for (var i = 0, row; (row = rows[i]); i++) {
                tbody.append("<tr><td><input type='text' value='" + row.name + "'></td><td>" + row.type + "</td><td>" + row.family + "</td></tr>");
                listeners.push(tbody.find("tr:last-child input").ankorBindInputValue(ref.appendIndex(i).append("name")));
            }
        });

        //Refresh tabs and select newly created tab
        $("#tabs").tabs("refresh");
        $("#tabs").tabs("option", "active", $("#tabs > div").length - 1);
    };

    return AnimalSearchTab;
});
