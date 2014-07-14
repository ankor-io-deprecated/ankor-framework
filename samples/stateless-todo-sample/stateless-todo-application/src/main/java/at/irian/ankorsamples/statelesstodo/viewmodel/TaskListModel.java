package at.irian.ankorsamples.statelesstodo.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.state.StateHolder;
import at.irian.ankorsamples.statelesstodo.domain.Filter;
import at.irian.ankorsamples.statelesstodo.domain.Task;
import at.irian.ankorsamples.statelesstodo.domain.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class TaskListModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    // helper fields

    private final Ref modelRef;
    private final TaskRepository taskRepository;

    // state fields

    @StateHolder
    private Filter filter;
    
    @StateHolder
    private int editing;
    
    // calculated fields

    private List<TaskModel> tasks;

    private boolean footerVisibility;
    private boolean clearButtonVisibility;

    private int itemsLeft;
    private int itemsComplete;

    private boolean filterAllSelected;
    private boolean filterActiveSelected;
    private boolean filterCompletedSelected;
    
    private boolean toggleAll;

    private String itemsLeftText;
    private String itemsCompleteText;

    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);
        this.modelRef = modelRef;
        this.taskRepository = taskRepository;

        // init state fields
        filter = Filter.all;

        initCalculatedFields();
    }

    private void initCalculatedFields() {
        LOG.info("Init calculated fields");

        tasks = queryTaskList(filter);
        
        editing = -1;

        itemsLeft = taskRepository.countTasks(Filter.active);
        itemsComplete = taskRepository.countTasks(Filter.completed);

        filterAllSelected = calcAllButtonSelected(filter);
        filterActiveSelected = calcActiveButtonSelected(filter);
        filterCompletedSelected = calcCompletedButtonSelected(filter);

        itemsLeftText = calcItemsLeftText(itemsLeft);
        itemsCompleteText = calcItemsCompleteText(itemsComplete);
        footerVisibility = calcFooterVisibility(itemsLeft, itemsComplete);
        clearButtonVisibility = calcClearButtonVisibility(itemsComplete);

        toggleAll = calcToggleAllSelected(itemsLeft);
    }

    @ChangeListener(pattern = {"root.model.tasks.(*).title",
                               "root.model.tasks.(*).completed"})
    public void onTaskChanged(Ref ref) {
        LOG.info("Task {} changed", ref.path());
        TaskModel taskModel = ref.getValue();
        Task task = taskRepository.getTaskById(taskModel.getId());
        task.setTitle(taskModel.getTitle());
        task.setCompleted(taskModel.isCompleted());
        taskRepository.updateTask(task);
        updateTasksList();
    }
    
    @ChangeListener(pattern = "root.model.filter")
    public void onFilterChanged() {
        LOG.info("Filter changed to {}", filter);
        updateTasksList();
        modelRef.appendPath("filterAllSelected").setValue(calcAllButtonSelected(filter));
        modelRef.appendPath("filterActiveSelected").setValue(calcActiveButtonSelected(filter));
        modelRef.appendPath("filterCompletedSelected").setValue(calcCompletedButtonSelected(filter));
    }

    @ChangeListener(pattern = "root.model.itemsLeft")
    public void onItemsLeftChanged() {
        LOG.info("ItemsLeft changed to {}", itemsLeft);
        modelRef.appendPath("itemsLeftText").setValue(calcItemsLeftText(itemsLeft));
        modelRef.appendPath("footerVisibility").setValue(calcFooterVisibility(itemsLeft, itemsComplete));
        modelRef.appendPath("toggleAll").setValue(calcToggleAllSelected(itemsLeft));
    }

    @ChangeListener(pattern = "root.model.itemsComplete")
    public void onItemsCompleteChanged() {
        LOG.info("ItemsComplete changed to {}", itemsComplete);
        modelRef.appendPath("itemsCompleteText").setValue(calcItemsCompleteText(itemsComplete));
        modelRef.appendPath("footerVisibility").setValue(calcFooterVisibility(itemsLeft, itemsComplete));
        modelRef.appendPath("clearButtonVisibility").setValue(calcClearButtonVisibility(itemsComplete));
    }

    @ChangeListener(pattern = "root.model.tasks")
    public void onTaskListChanged() {
        LOG.info("TaskList changed");
        modelRef.appendPath("itemsLeft").setValue(taskRepository.countTasks(Filter.active));
        modelRef.appendPath("itemsComplete").setValue(taskRepository.countTasks(Filter.completed));
    }

    @ActionListener
    public void newTask(@Param("title") String title) {
        LOG.info("Add new task with title '{}'", title);
        taskRepository.insertTask(title, false);
        updateTasksList();
    }

    @ActionListener
    public void deleteTask(@Param("id") String taskId) {
        LOG.info("Delete task {}", taskId);
        taskRepository.deleteTask(taskId);
        updateTasksList();
    }

    @ActionListener
    public void toggleAll(@Param("toggleAll") boolean completed) {
        LOG.info("Setting 'completed' flag of all filtered tasks to {}", completed);
        List<Task> tasks = taskRepository.queryTasks(Filter.all);
        for (Task task : tasks) {
            task.setCompleted(completed);
            taskRepository.updateTask(task);
        }
        updateTasksList();
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clear all completed tasks");
        List<Task> tasks = taskRepository.queryTasks(Filter.all);
        for (Task task : tasks) {
            if (task.isCompleted()) {
                taskRepository.deleteTask(task.getId());
            }
        }
        updateTasksList();
    }

    private void updateTasksList() {
        modelRef.appendPath("tasks").setValue(queryTaskList(filter));
    }

    /* ---------------- *
     *  Business logic  *
     * ---------------- */

    private List<TaskModel> queryTaskList(Filter filter) {
        List<Task> tasks = taskRepository.queryTasks(filter);
        ArrayList<TaskModel> taskModelList = new ArrayList<>(tasks.size());
        int i = 0;
        for (Task task : tasks) {
            taskModelList.add(new TaskModel(modelRef.appendPath("tasks").appendIndex(i), task));
            i++;
        }
        return taskModelList;
    }


    private boolean calcAllButtonSelected(Filter filter) {
        return filter == Filter.all;
    }

    private boolean calcActiveButtonSelected(Filter filter) {
        return filter == Filter.active;
    }

    private boolean calcCompletedButtonSelected(Filter filter) {
        return filter == Filter.completed;
    }

    private String calcItemsLeftText(int itemsLeft) {
        return (itemsLeft == 1) ? "item left" : "items left";
    }

    private String calcItemsCompleteText(int itemsComplete) {
        return String.format("Clear completed (%d)", itemsComplete);
    }

    private boolean calcFooterVisibility(int itemsLeft, int itemsComplete) {
        return itemsLeft != 0 || itemsComplete != 0;
    }

    private boolean calcClearButtonVisibility(int itemsComplete) {
        return itemsComplete != 0;
    }

    private boolean calcToggleAllSelected(int itemsLeft) {
        return itemsLeft == 0;
    }

    /* ------------------- *
     *  Getters & Setters  *
     * ------------------- */

    public String getFilter() {
        return filter.toString();
    }

    public void setFilter(String filter) {
        this.filter = Filter.valueOf(filter);
        initCalculatedFields();
    }

    public int getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(int itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public boolean getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(boolean footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public int getItemsComplete() {
        return itemsComplete;
    }

    public void setItemsComplete(int itemsComplete) {
        this.itemsComplete = itemsComplete;
    }

    public String getItemsCompleteText() {
        return itemsCompleteText;
    }

    public void setItemsCompleteText(String itemsCompleteText) {
        this.itemsCompleteText = itemsCompleteText;
    }

    public boolean getClearButtonVisibility() {
        return clearButtonVisibility;
    }

    public void setClearButtonVisibility(boolean clearButtonVisibility) {
        this.clearButtonVisibility = clearButtonVisibility;
    }

    public boolean getToggleAll() {
        return toggleAll;
    }

    public void setToggleAll(boolean toggleAll) {
        this.toggleAll = toggleAll;
    }

    public boolean getFilterAllSelected() {
        return filterAllSelected;
    }

    public void setFilterAllSelected(boolean filterAllSelected) {
        this.filterAllSelected = filterAllSelected;
    }

    public boolean getFilterActiveSelected() {
        return filterActiveSelected;
    }

    public void setFilterActiveSelected(boolean filterActiveSelected) {
        this.filterActiveSelected = filterActiveSelected;
    }

    public boolean getFilterCompletedSelected() {
        return filterCompletedSelected;
    }

    public void setFilterCompletedSelected(boolean filterCompletedSelected) {
        this.filterCompletedSelected = filterCompletedSelected;
    }

    public String getItemsLeftText() {
        return itemsLeftText;
    }

    public void setItemsLeftText(String itemsLeftText) {
        this.itemsLeftText = itemsLeftText;
    }

    public int getEditing() {
        return editing;
    }

    public void setEditing(int editing) {
        this.editing = editing;
    }
}
