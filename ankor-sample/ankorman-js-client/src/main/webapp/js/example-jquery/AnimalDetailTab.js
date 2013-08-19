define([
    "jquery",
    "text!./templates/AnimalDetailTab.html"
], function($, template) {
    template = new EJS({
        text: template
    });

    var AnimalDetailTab = function(ref) {
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
        body.find("input.name").ankorBindInputValue(ref.append("model.animal.name"));
        body.find("span.nameStatus").ankorBindInnerHTML(ref.append("model.nameStatus"));
        body.find("select.type").ankorBindSelectItems(ref.append("model.selectItems.types"), {
            emptyOption: true
        });
        body.find("select.type").ankorBindInputValue(ref.append("model.animal.type"));
        body.find("select.family").ankorBindSelectItems(ref.append("model.selectItems.families"), {
            emptyOption: true
        });
        body.find("select.family").ankorBindInputValue(ref.append("model.animal.family"));
        body.find("button").click(function() {
            ref.append("model").fire("save");
        });

        //Refresh tabs and select newly created tab
        $("#tabs").tabs("refresh");
        $("#tabs").tabs("option", "active", $("#tabs > div").length - 1);
    };

    return AnimalDetailTab;
});
