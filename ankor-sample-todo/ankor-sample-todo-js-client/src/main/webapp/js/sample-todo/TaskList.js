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

        var ENTER_KEY = 13;

        // Proposed additional adapters

        // XXX: Needs better name
        $.fn.ankorBind1 = function(functionName, ref) {
            var element = this.first();

            return ref.addPropChangeListener(function(ref) {
                $.fn[functionName].call(element, ref.getValue());
            });
        };

        // XXX: Needs better name
        $.fn.ankorBind2 = function(functionName, propertyName, ref) {
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
            return $.fn.ankorBind2.call(this, "prop", propertyName, ref);
        };

        $.fn.ankorBindAttr = function(propertyName, ref) {
            return $.fn.ankorBind2.call(this, "attr", propertyName, ref);
        };

        $.fn.ankorBindData = function(propertyName, ref) {
            return $.fn.ankorBind2.call(this, "data", propertyName, ref);
        };

        $.fn.ankorBindToggle = function(ref) {
            return $.fn.ankorBind1.call(this, "toggle", ref);
        }

        $.fn.ankorBindToggleClass = function(clazz, ref) {
            return $.fn.ankorBind2.call(this, "toggleClass", clazz, ref);
        }

        $("#app").html(template.render({}));

        $('#todo-count > strong').ankorBindText(modelRef.append("itemsLeft"));
        $('#todo-count > span').ankorBindText(modelRef.append("itemsLeftText"));

        $('#main').ankorBindToggle(modelRef.append("footerVisibility"));
        $('#footer').ankorBindToggle(modelRef.append("footerVisibility"));

        var $clearCompleted = $('#clear-completed');
        $clearCompleted.ankorBindText(modelRef.append("itemsCompleteText"));
        $clearCompleted.ankorBindToggle(modelRef.append("clearButtonVisibility"));

        $('#filter-all')
            .on('click', function() { modelRef.append("filter").setValue("all"); })
            .ankorBindToggleClass("selected", modelRef.append("filterAllSelected"));
        $('#filter-active')
            .on('click', function() { modelRef.append("filter").setValue("active"); })
            .ankorBindToggleClass("selected", modelRef.append("filterActiveSelected"));
        $('#filter-completed')
            .on('click', function() { modelRef.append("filter").setValue("completed"); })
            .ankorBindToggleClass("selected", modelRef.append("filterCompletedSelected"));
        $("#toggle-all")
            .on('click', function() { modelRef.fire("toggleAll") })
            .ankorBindProp("checked", modelRef.append("toggleAll"));

        modelRef.append("editing").addPropChangeListener(function(ref) {
            $('#todo-list > li').removeClass("editing");

            var index = ref.getValue();
            if (index >= 0) {
                $('#todo-list > li').eq(index).addClass('editing').find('.edit').focus().select();
            }
        });

        var bindingContext = []
        modelRef.append("tasks").addPropChangeListener(function(ref) {
            //Cleanup
            var $todoList = $('#todo-list').empty(); // removes event handlers
            for (var i = 0, listener; (listener = bindingContext[i]); i++) {
                listener.remove();
            }
            bindingContext = [];

            var listRef = function(index) {
                return ref.appendIndex(index);
            }

            var tasks = ref.getValue();
            tasks.forEach
            for (var i = 0, task; (task = tasks[i]); i++) {
                // XXX: Task view ?
                $todoList.append(taskTemplate.render(task));

                var $task = $('#task-'+task.id);

                bindingContext.push(
                    $task.find('label')
                        .ankorBindText(listRef(i).append("title")));

                bindingContext.push(
                    $task.find('.edit')
                        .ankorBindText(listRef(i).append("title")));

                (function(index) {
                    $task.find('.toggle').on("click", function() {
                        modelRef.fire({
                            name: 'toggleTask',
                            params: { index: index }
                        });
                    });

                    $task.find('.destroy').on("click", function() {
                        modelRef.fire({
                            name: 'deleteTask',
                            params: { index: index }
                        });
                    });

                    $task.on("dblclick", function() {
                        modelRef.append("editing").setValue(index);
                    });

                    $task.find('.edit')
                        .on('focusout', function() {
                            modelRef.append("editing").setValue(-1);
                        })
                        .on('keyup', function(e) {
                            var title = $(e.currentTarget).val();
                            if (e.keyCode === ENTER_KEY) {
                                modelRef.fire({
                                    name: "editTask",
                                    params: {
                                        index: index,
                                        title: title
                                    }
                                });
                                modelRef.append("editing").setValue(-1);
                            }
                        });
                })(i);
            }
        });
        $("#new-todo").on('keyup', function(e) {
            var title = $(e.currentTarget).val();
            if (e.keyCode === ENTER_KEY &&  title != "") {
                modelRef.fire({
                    name: "newTask",
                    params: {
                        title: title
                    }
                });
                $(e.currentTarget).val('');
            }
        });

        $clearCompleted.on("click", function(e) {
            modelRef.fire("clearTasks");
        });
    };

    return TaskList;
});
