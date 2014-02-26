package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.task.Filter;
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
    private final Ref modelRef;
    private Filter filter = Filter.all;
    private List<TaskModel> tasks;

    private Boolean footerVisibility = false;
    private Integer itemsLeft = 0;
    private String itemsLeftText;

    private Boolean clearButtonVisibility = false;
    private Integer itemsComplete = 0;
    private String itemsCompleteText;
    
    private Boolean filterAllSelected = true;
    private Boolean filterActiveSelected = false;
    private Boolean filterCompletedSelected = false;
    
    private Boolean toggleAll = false;

    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);
        this.modelRef = modelRef;
        this.taskRepository = taskRepository;
        
        tasks = new ArrayList<>(fetchTaskModels(filter));
        this.itemsLeftText = itemsLeftText(itemsLeft);
        this.itemsCompleteText = itemsCompleteText(itemsComplete);
    }

    @ChangeListener(pattern = "root.model.tasks.*.completed")
    public void completedChanged() {
        updateItemsCount();
        reloadTasks(filter);
    }

    @ChangeListener(pattern = {
            "root.model.tasks.(*).title",
            "root.model.tasks.(*).completed"})
    public void saveTask(Ref ref) {
        Task model = ref.getValue();
        taskRepository.saveTask(model);
    }

    @ChangeListener(pattern = "root.model.filter")
    public void updateFilterSelected() {
        modelRef.appendPath("filterAllSelected").setValue(filter.equals(Filter.all));
        modelRef.appendPath("filterActiveSelected").setValue(filter.equals(Filter.active));
        modelRef.appendPath("filterCompletedSelected").setValue(filter.equals(Filter.completed));
        reloadTasks(filter);
    }

    @ChangeListener(pattern = {
            "root.model.itemsLeft",
            "root.model.itemsComplete"})
    public void updateFooterVisibility() {
        modelRef.appendPath("footerVisibility").setValue(itemsLeft != 0 || itemsComplete != 0);
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

    @ActionListener
    public void newTask(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        updateItemsCount();

        if (!filter.equals(Filter.completed)) {
            TaskModel model = new TaskModel(task);
            tasksRef().add(model);
        }
    }

    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        LOG.info("Deleting task {}", index);

        Task task = tasks.get(index);
        taskRepository.deleteTask(task);

        updateItemsCount();
        tasksRef().delete(index);
    }

    @ActionListener
    public void toggleAll(@Param("toggleAll") final boolean toggleAll) {
        LOG.info("Setting completed of all tasks to {}", toggleAll);

        taskRepository.toggleAll(toggleAll);
        updateItemsCount();
        reloadTasks(filter);
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clearing completed tasks");

        taskRepository.clearTasks();
        updateItemsCount();
        reloadTasks(filter);
    }

    private void updateItemsCount() {
        modelRef.appendPath("itemsLeft").setValue(taskRepository.fetchActiveTasks().size());
        modelRef.appendPath("itemsComplete").setValue(taskRepository.fetchCompletedTasks().size());
    }

    // helper for dealing with list refs
    private CollectionRef tasksRef() {
        return modelRef.appendPath("tasks").toCollectionRef();
    }

    private void reloadTasks(Filter filter) {
        LOG.info("reloading tasks");
        tasksRef().setValue(fetchTaskModels(filter));
        // (new ListDiff<>(tasks, fetchTaskModels(filter))).withThreshold(10).applyChangesTo(tasksRef());
    }

    private List<TaskModel> fetchTaskModels(Filter filter) {
        List<Task> tasks = taskRepository.fetchTasks(filter);
        List<TaskModel> res = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            TaskModel model = new TaskModel(t);
            res.add(model);
        }

        return res;
    }

    private String itemsLeftText(int itemsLeft) {
        return (itemsLeft == 1) ? "item left" : "items left";
    }

    private String itemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }

    /*
     *********************
     * Getters & Setters *
     *********************
     */
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
        return filter.toString();
    }

    public void setFilter(String filter) {
        this.filter = Filter.valueOf(filter);
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
