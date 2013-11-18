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
        $("#tabs ul").append("<li class='" + tab.id + "' data-tabid='" + tab.id + "'><a href='#tab-" + tab.id + "'><span></span></a><a href='#' class='close'>x</a></li>");
        $("#tabs ul li." + tab.id + " span").ankorBindInnerHTML(ref.append("name"));
        $("#tabs ul li." + tab.id + " a.close").click(function() {
            ref.del();
        });

        //Create tab body
        $("#tabs").append(template.render({
            id: tab.id
        }));

        //Bind tab body fields
        var body = $("#tabs #tab-" + tab.id);
        body.find("input.name").ankorBindInputValue(ref.append("model.filter.name"));
        body.find("select.type").ankorBindSelectItems(ref.append("model.selectItems.types"), {
            emptyOption: false
        });
        body.find("select.type").ankorBindInputValue(ref.append("model.filter.type"));
        body.find("select.family").ankorBindSelectItems(ref.append("model.selectItems.families"), {
            emptyOption: false
        });
        body.find("select.family").ankorBindInputValue(ref.append("model.filter.family"));
        body.find("button.save").click(function() {
            ref.append("model").fire("save");
        });

        //Table handling
        var animalsRef = ref.append("model.animals");
        var listeners = [];
        var page = 0;
        var rowsPerPage = 10;

        var updatePage = function() {
            var tbody = body.find("tbody");
            var tableSize = animalsRef.size();

            //Cleanup
            tbody.html("");
            for (var i = 0, listener; (listener = listeners[i]); i++) {
                listener.remove();
            }
            listeners = [];

            //Render new table
            for (var i = page * rowsPerPage; i < Math.min(tableSize, page * rowsPerPage + rowsPerPage); i++) {
                var rowRef = animalsRef.appendIndex(i);
                var row = rowRef.getValue();
                tbody.append("<tr><td><input type='text' value='" + row.name + "'></td><td><span class='type'></span></td><td><span class='family'></span></td></tr>");
                listeners.push(tbody.find("tr:last-child input").ankorBindInputValue(rowRef.append("name")));
                listeners.push(tbody.find("tr:last-child span.type").ankorBindInnerHTML(rowRef.append("type")));
                listeners.push(tbody.find("tr:last-child span.family").ankorBindInnerHTML(rowRef.append("family")));
            }

            //Update paging button state
            if (page == 0) {
                body.find(".prev").attr("disabled", "true");
            }
            else {
                body.find(".prev").removeAttr("disabled");
            }
            if (page < Math.floor(tableSize / rowsPerPage)) {
                body.find(".next").removeAttr("disabled");
            }
            else {
                body.find(".next").attr("disabled", "true");
            }

            body.find(".totalPages").html(Math.ceil(tableSize / rowsPerPage));
            body.find(".currentPage").html(page + 1);
        };
        animalsRef.addPropChangeListener(function(ref) {
            body.find(".tableSize").html(animalsRef.size());
            page = 0;
            updatePage();
        });
        body.find(".prev").click(function() {
            page--;
            updatePage();
        });
        body.find(".next").click(function() {
            page++;
            updatePage();
        });

        //Refresh tabs and select newly created tab
        $("#tabs").tabs("refresh");
        $("#tabs").tabs("option", "active", $("#tabs > div").length - 1);
    };

    return AnimalSearchTab;
});
