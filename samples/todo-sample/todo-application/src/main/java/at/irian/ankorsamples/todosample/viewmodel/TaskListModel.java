package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.Filter;
import at.irian.ankorsamples.todosample.domain.Task;
import at.irian.ankorsamples.todosample.domain.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class TaskListModel {

    private final Ref modelRef;

    private final TaskRepository taskRepository;

    private Boolean footerVisibility = false;
    private Integer itemsLeft = 0;
    private String itemsLeftText;

    private Boolean clearButtonVisibility = false;
    private Integer itemsComplete = 0;
    private String itemsCompleteText;
    private Boolean toggleAll = false;

    private List<TaskModel> tasks = new ArrayList<>();

    private Filter filter = Filter.all;
    private Boolean filterAllSelected = true;
    private Boolean filterActiveSelected = false;
    private Boolean filterCompletedSelected = false;

    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);

        this.modelRef = modelRef;
        this.taskRepository = taskRepository;

        this.itemsLeftText = itemsLeftText(itemsLeft);
        this.itemsCompleteText = itemsCompleteText(itemsComplete);
    }

    @ActionListener
    public void newTask(@Param("title") final String title) {
        Task task = new Task(title);
        taskRepository.saveTask(task);

        updateItemsCount();

        if (!filter.equals(Filter.completed)) {
            TaskModel model = new TaskModel(task);
            tasksRef().add(model);
        }
    }

    private CollectionRef tasksRef() {
        return modelRef.appendPath("tasks").toCollectionRef();
    }

    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        Task task = tasks.get(index);
        taskRepository.deleteTask(task);

        updateItemsCount();

        tasksRef().delete(index);
    }

    @ActionListener
    public void clearTasks() {
        taskRepository.clearTasks();
        updateItemsCount();
        reloadTasks(filter);
    }

    @ActionListener
    public void toggleAll(@Param("toggleAll") final boolean toggleAll) {
        taskRepository.toggleAll(toggleAll);
        updateItemsCount();
        reloadTasks(filter);
    }

    @ChangeListener(pattern = "root.model.itemsLeft")
    public void itemsLeftChanged() {
        modelRef.appendPath("itemsLeftText").setValue(itemsLeftText(itemsLeft));
        modelRef.appendPath("toggleAll").setValue(itemsLeft == 0);
    }

    @ChangeListener(pattern = "root.model.itemsComplete")
    public void updateClearButton() {
        modelRef.appendPath("clearButtonVisibility").setValue(itemsComplete != 0);
        modelRef.appendPath("itemsCompleteText").setValue(itemsCompleteText(itemsComplete));
    }

    @ChangeListener(pattern = {
            "root.model.itemsLeft",
            "root.model.itemsComplete"})
    public void updateFooterVisibility() {
        modelRef.appendPath("footerVisibility").setValue(itemsLeft != 0 || itemsComplete != 0);
    }

    @ChangeListener(pattern = "root.model.filter")
    public void updateFilterSelected() {
        modelRef.appendPath("filterAllSelected").setValue(filter.equals(Filter.all));
        modelRef.appendPath("filterActiveSelected").setValue(filter.equals(Filter.active));
        modelRef.appendPath("filterCompletedSelected").setValue(filter.equals(Filter.completed));
        reloadTasks(filter);
    }

    @ChangeListener(pattern = {
            "root.model.tasks.(*).completed",
            "root.model.tasks.(*).title"})
    public void saveTask(Ref ref) {
        Task model = ref.getValue();
        taskRepository.saveTask(model);

        updateItemsCount();
        reloadTasks(filter);
    }

    private void reloadTasks(Filter filter) {
        List<Task> tasks = taskRepository.fetchTasks(filter);
        List<TaskModel> taskModels = mapTasksToTaskModels(tasks);
        tasksRef().setValue(taskModels);
    }

    private List<TaskModel> mapTasksToTaskModels(List<Task> tasks) {
        List<TaskModel> res = new ArrayList<>(tasks.size());
        for (Task t : tasks) {
            res.add(new TaskModel(t));
        }
        return res;
    }

    private String itemsLeftText(int itemsLeft) {
        return (itemsLeft == 1) ? "item left" : "items left";
    }

    private String itemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }

    private void updateItemsCount() {
        modelRef.appendPath("itemsLeft").setValue(taskRepository.fetchActiveTasks().size());
        modelRef.appendPath("itemsComplete").setValue(taskRepository.fetchCompletedTasks().size());
    }

    public Boolean getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(Boolean footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public Integer getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(Integer itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public String getItemsLeftText() {
        return itemsLeftText;
    }

    public void setItemsLeftText(String itemsLeftText) {
        this.itemsLeftText = itemsLeftText;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public Boolean getClearButtonVisibility() {
        return clearButtonVisibility;
    }

    public void setClearButtonVisibility(Boolean clearButtonVisibility) {
        this.clearButtonVisibility = clearButtonVisibility;
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

    public Boolean getToggleAll() {
        return toggleAll;
    }

    public void setToggleAll(Boolean toggleAll) {
        this.toggleAll = toggleAll;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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
}
