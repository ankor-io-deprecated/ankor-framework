define([
    "jquery",
    "ejs",
    "text!./templates/TaskList.html",
    "text!./templates/Task.html"
], function($, EJS, template, taskTemplate) {
    template = new EJS({
        text: template
    });

    taskTemplate = new EJS({
        text: taskTemplate
    });

    var TaskList = function(modelRef) {

        // Helpers
        $.fn.addOrRemoveClass = function(clazz, isSelected) {
            var fun = isSelected ? $.fn.addClass : $.fn.removeClass;
            fun.call(this, clazz);
        };

        $.fn.addOrRemoveSelectedClass = function(isSelected) {
            $.fn.addOrRemoveClass.call(this, "selected", isSelected);
        };

        // XXX: A more general approach to bindings
        $.fn.ankorBind = function(fun, ref) {
            var element = this.first();
            return ref.addPropChangeListener(function() {
                if ($.fn[fun] == null) {
                    console.log("Error: $.fn has no function " + fun);
                } else {
                    $.fn[fun].call(element, ref.getValue());
                }
            });
        }

        $("#app").html(template.render({}));

        $('#todo-count > strong').ankorBind("html", modelRef.append("itemsLeft"));
        $('#todo-count > span').ankorBind("html",  modelRef.append("itemsLeftText"));

        $('#main').ankorBind("toggle", modelRef.append("footerVisibility"));
        $('#footer').ankorBind("toggle", modelRef.append("footerVisibility"));

        var $clearCompleted = $('#clear-completed');
        $clearCompleted.ankorBind("html", modelRef.append("itemsCompleteText"));
        $clearCompleted.ankorBind("toggle", modelRef.append("clearButtonVisibility"));

        $('#filter-all').ankorBind("addOrRemoveSelectedClass", modelRef.append("filterAllSelected"));
        $('#filter-active').ankorBind("addOrRemoveSelectedClass", modelRef.append("filterActiveSelected"));
        $('#filter-completed').ankorBind("addOrRemoveSelectedClass", modelRef.append("filterCompletedSelected"));

        modelRef.append("toggleAll").addPropChangeListener(function(ref) {
            $("#toggle-all").prop("checked", ref.getValue());
        });

        var bindingContext = []
        modelRef.append("tasks").addPropChangeListener(function(ref) {
            //Cleanup
            var $todoList = $('#todo-list').html('');
            for (var i = 0, listener; (listener = bindingContext[i]); i++) {
                listener.remove();
            }
            bindingContext = [];

            var listRef = function(index) {
                return ref.appendIndex(index);
            }

            var tasks = ref.getValue();
            for (var i = 0, task; (task = tasks[i]); i++) {
                // XXX: Task view ?
                $todoList.append(taskTemplate.render(task));

                bindingContext.push(
                    $('#task-'+task.id+' label')
                        .ankorBind("html", listRef(i).append("title")));

                bindingContext.push(
                    $('#task-'+task.id+' .edit')
                        .ankorBind("html", listRef(i).append("title")));
            }
        });

        // TODO: Params in Actions currently not supported
        // var ENTER_KEY = 13;
        // var $newTask = $("#new-todo");
        // $newTask.keyup(function(e) {
        //     if (e.keyCode === ENTER_KEY && $newTask.val() != "") {
        //         modelRef.fire("newTask", { title: $newTask.val() });
        //     }
        // });
    };

    return TaskList;
});
