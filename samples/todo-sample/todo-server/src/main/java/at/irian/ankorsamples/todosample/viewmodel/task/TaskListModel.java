package at.irian.ankorsamples.todosample.viewmodel.task;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankor.viewmodel.diff.ListDiff;
import at.irian.ankorsamples.todosample.domain.task.Task;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class TaskListModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    @AnkorIgnore
    private final TaskRepository taskRepository;
    @AnkorIgnore
    private final Ref thisRef;

    private List<TaskModel> tasks;

    private String filter;

    private Integer itemsLeft;
    private String itemsLeftText;
    private Boolean footerVisibility;

    private Integer itemsComplete;
    private String itemsCompleteText;
    private Boolean clearButtonVisibility;

    private Boolean toggleAll;

    private Boolean filterAllSelected;
    private Boolean filterActiveSelected;
    private Boolean filterCompletedSelected;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, viewModelRef);

        this.taskRepository = taskRepository;
        this.thisRef = viewModelRef;

        filter = Filter.all.toString();
        filterAllSelected = (true);
        filterActiveSelected = (false);
        filterCompletedSelected = (false);

        tasks = new ArrayList<>(fetchTasksData());

        itemsLeft = taskRepository.getActiveTasks().size();
        itemsLeftText = (itemsLeftText(itemsLeft));
        footerVisibility = taskRepository.getTasks().size() != 0;

        itemsComplete = (taskRepository.getCompletedTasks().size());
        itemsCompleteText = (itemsCompleteText(itemsComplete));
        clearButtonVisibility = (itemsComplete != 0);

        toggleAll = (false);

        RefListeners.addTreeChangeListener(tasksRef(), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String name = changedProperty.propertyName();

                if ("completed".equals(name) || "title".equals(name)) {
                    TaskModel model = changedProperty.parent().getValue();
                    Task task = model.getTask();
                    TaskListModel.this.taskRepository.saveTask(task);

                    if ("completed".equals(name)) {
                        updateTasksData();
                    }
                }
            }
        });
    }

    @ChangeListener(pattern = "root.model.filter")
    public void filterChanged() {
        LOG.info("reloading tasks");

        thisRef.appendPath("filterAllSelected").setValue(filter.equals("all"));
        thisRef.appendPath("filterActiveSelected").setValue(filter.equals("active"));
        thisRef.appendPath("filterCompletedSelected").setValue(filter.equals("completed"));
        updateTasksData();
    }

    @ChangeListener(pattern = {
            "root.model.filterAllSelected",
            "root.model.filterActiveSelected",
            "root.model.filterCompletedSelected" })
    public void reloadTasks() {
        if (filterAllSelected) {
            thisRef.appendPath("filter").setValue(Filter.all.toString());
        } else if (filterActiveSelected) {
            thisRef.appendPath("filter").setValue(Filter.active.toString());
        } else if (filterCompletedSelected) {
            thisRef.appendPath("filter").setValue(Filter.completed.toString());
        }
    }

    @ChangeListener(pattern = {
            "root.model.itemsLeft",
            "root.model.itemsComplete" })
    public void updateFooterVisibility() {
        thisRef.appendPath("footerVisibility").setValue(taskRepository.getTasks().size() != 0);
    }

    @ChangeListener(pattern="root.model.itemsLeft")
    public void itemsLeftChanged() {
        thisRef.appendPath("itemsLeftText").setValue(itemsLeftText(itemsLeft));
        thisRef.appendPath("toggleAll").setValue(itemsLeft == 0);
    }

    @ChangeListener(pattern="root.model.itemsComplete")
    public void updateClearButton() {
        thisRef.appendPath("clearButtonVisibility").setValue(itemsComplete != 0);
        thisRef.appendPath("itemsCompleteText").setValue(itemsCompleteText(itemsComplete));
    }

    @ActionListener
    public void newTask(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);
        thisRef.appendPath("itemsLeft").setValue(taskRepository.getActiveTasks().size()); // XXX: incValue?

        if (!Filter.valueOf(filter).equals(Filter.completed)) {
            int index = tasks.size();
            TaskModel model = new TaskModel(task);
            tasksRef().toCollectionRef().insert(index, model);
        }
    }

    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        LOG.info("Deleting task {}", index);

        Task task = tasks.get(index).getTask();
        taskRepository.deleteTask(task);
        thisRef.appendPath("itemsLeft").setValue(taskRepository.getActiveTasks().size());
        thisRef.appendPath("itemsComplete").setValue(taskRepository.getCompletedTasks().size());

        tasksRef(index).delete();
    }

    @ActionListener
    public void toggleAll() {
        LOG.info("Setting completed of all tasks to {}", toggleAll);

        for (Task t : taskRepository.getTasks()) {
            t.setCompleted(toggleAll);
            taskRepository.saveTask(t);
        }

        updateTasksData();
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clearing completed tasks");

        taskRepository.clearTasks();
        updateTasksData();
    }

    @ChangeListener(pattern = "root.model.tasks[*].completed")
    public void completedChanged() {
        LOG.info("completed changed");
    }

    @ChangeListener(pattern = "root.model.tasks[*].title")
    public void titleChanged() {
        LOG.info("title changed");
    }

    // helper for dealing with list refs
    private Ref tasksRef() {
        return thisRef.appendPath("tasks");
    }

    // helper for dealing with list refs
    private Ref tasksRef(int index) {
        return thisRef.appendPath("tasks").appendIndex(index);
    }

    private List<TaskModel> fetchTasksData() {
        Filter filterEnum = Filter.valueOf(filter);
        List<Task> tasks = taskRepository.filterTasks(filterEnum);
        List<TaskModel> res = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            TaskModel model = new TaskModel(t);
            res.add(model);
        }

        return res;
    }

    private void updateTasksData() {
        new ListDiff<>(tasks, fetchTasksData()).withThreshold(10).applyChangesTo(tasksRef());
        thisRef.appendPath("itemsLeft").setValue(taskRepository.getActiveTasks().size());
        thisRef.appendPath("itemsComplete").setValue(taskRepository.getCompletedTasks().size());
    }

    private String itemsLeftText(int itemsLeft) {
        String text;
        if (itemsLeft == 1) {
            text = "item left";
        } else {
            text = "items left";
        }
        return text;
    }

    private String itemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }

    public Integer getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(Integer itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Boolean getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(Boolean footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public Integer getItemsComplete() {
        return itemsComplete;
    }

    public void setItemsComplete(Integer itemsComplete) {
        this.itemsComplete = itemsComplete;
    }

    public String getItemsCompleteText() {
        return itemsCompleteText;
    }

    public void setItemsCompleteText(String itemsCompleteText) {
        this.itemsCompleteText = itemsCompleteText;
    }

    public Boolean getClearButtonVisibility() {
        return clearButtonVisibility;
    }

    public void setClearButtonVisibility(Boolean clearButtonVisibility) {
        this.clearButtonVisibility = clearButtonVisibility;
    }

    public Boolean getToggleAll() {
        return toggleAll;
    }

    public void setToggleAll(Boolean toggleAll) {
        this.toggleAll = toggleAll;
    }

    public Boolean getFilterAllSelected() {
        return filterAllSelected;
    }

    public void setFilterAllSelected(Boolean filterAllSelected) {
        this.filterAllSelected = filterAllSelected;
    }

    public Boolean getFilterActiveSelected() {
        return filterActiveSelected;
    }

    public void setFilterActiveSelected(Boolean filterActiveSelected) {
        this.filterActiveSelected = filterActiveSelected;
    }

    public Boolean getFilterCompletedSelected() {
        return filterCompletedSelected;
    }

    public void setFilterCompletedSelected(Boolean filterCompletedSelected) {
        this.filterCompletedSelected = filterCompletedSelected;
    }

    public String getItemsLeftText() {
        return itemsLeftText;
    }

    public void setItemsLeftText(String itemsLeftText) {
        this.itemsLeftText = itemsLeftText;
    }
}
