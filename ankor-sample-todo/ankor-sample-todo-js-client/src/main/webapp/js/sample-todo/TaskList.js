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

        $.fn.ankorBind1 = function(functionName, ref) {
            var element = this.first();

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

        $.fn.ankorBindValue = function(functionName, propertyName, ref) {
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
            return $.fn.ankorBindValue.call(this, "prop", propertyName, ref);
        };

        $.fn.ankorBindAttr = function(propertyName, ref) {
            return $.fn.ankorBindValue.call(this, "attr", propertyName, ref);
        };

        $.fn.ankorBindData = function(propertyName, ref) {
            return $.fn.ankorBindValue.call(this, "data", propertyName, ref);
        };

        $.fn.ankorBindToggle = function(ref) {
            return $.fn.ankorBind1.call(this, "toggle", ref);
        }

        $.fn.ankorBindToggleClass = function(clazz, ref) {
            return $.fn.ankorBind2.call(this, "toggleClass", clazz, ref);
        }

        $("#app").html(template.render());

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

        var bindingContext = []
        modelRef.append("tasks").addPropChangeListener(function(ref) {
            //Cleanup
            var $todoList = $('#todo-list').empty(); // removes event handlers
            for (var i = 0, listener; (listener = bindingContext[i]); i++) {
                listener.remove();
            }
            bindingContext = [];

            function listRef(index) {
                return ref.appendIndex(index);
            }

            // Render tasks
            console.log("render tasks");
            var tasks = ref.getValue();
            for (var i = 0, model; (model = tasks[i]); i++) {
                (function(model) {
                    var index = model.index;

                    $todoList.append(taskTemplate.render(model));
                    var $task = $('#todo-list > li').eq(index);

                    bindingContext.push(
                        $task.find('label')
                            .ankorBindText(listRef(index).append("title")));

                    bindingContext.push(
                        $task.find('.edit')
                            .ankorBind1("val", listRef(index).append("title")));

                    bindingContext.push(
                        $task.find('.toggle')
                            .on("click", function() {
                                modelRef.fire({
                                    name: 'toggleTask',
                                    params: { index: index }
                                })
                            })
                            .ankorBindProp("checked", listRef(index).append("completed")));

                    bindingContext.push(
                        $task.on("dblclick", function() {
                            listRef(index).append("editing").setValue(true);
                            $task.find('.edit').focus().select();
                        })
                        .ankorBindToggleClass("editing", listRef(index).append("editing")));

                    $task.find('.destroy').on("click", function() {
                        modelRef.fire({
                            name: 'deleteTask',
                            params: { index: index }
                        });
                    });

                    $task.find('.edit')
                        .on('focusout', function() {
                            listRef(index).append("editing").setValue(false);
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
                                listRef(index).append("editing").setValue(false);
                            }
                        });
                })(model);
            }
        });

    };

    return TaskList;
});
