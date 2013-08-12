package at.irian.ankor.todosample.viewmodel.task;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankor.todosample.domain.task.Task;
import at.irian.ankor.todosample.server.TaskRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class TaskListModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    @JsonIgnore
    private final TaskRepository taskRepository;

    // TODO: Create custom class for view model lists
    private List<Task> tasks;

    private ViewModelProperty<String> filter;

    private ViewModelProperty<Integer> itemsLeft;
    private ViewModelProperty<Boolean> footerVisibility;

    @JsonIgnore
    private ViewModelProperty<Integer> itemsComplete;
    private ViewModelProperty<String> itemsCompleteText;
    private ViewModelProperty<Boolean> clearButtonVisibility;

    private ViewModelProperty<Boolean> toggleAll;

    private ViewModelProperty<Boolean> filterAllSelected;
    private ViewModelProperty<Boolean> filterActiveSelected;
    private ViewModelProperty<Boolean> filterCompletedSelected;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository) {
        super(viewModelRef);

        this.filter.set(Filter.all.toString()); // this.filter = new ViewModelProperty<>(viewModelRef, "filter", Filter.all.toString());
        this.filterAllSelected.set(true);
        this.filterActiveSelected.set(false);
        this.filterCompletedSelected.set(false);

        this.taskRepository = taskRepository;
        this.tasks = fetchTasksData();

        this.itemsLeft.set(taskRepository.getActiveTasks().size());
        this.updateFooterVisibility();

        this.itemsComplete.set(taskRepository.getCompletedTasks().size());
        this.updateClearButton();

        toggleAll.set(false);
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.itemsComplete",
            "**.<TaskListModel>.filterAllSelected",
            "**.<TaskListModel>.filterActiveSelected",
            "**.<TaskListModel>.filterCompletedSelected" })
    public void reloadTasks() {
        LOG.info("reloading tasks");

        if (filterAllSelected.get()) {
            filter.set(Filter.all.toString());
        } else if (filterActiveSelected.get()) {
            filter.set(Filter.active.toString());
        } else if (filterCompletedSelected.get()) {
            filter.set(Filter.completed.toString());
        }

        List<Task> tasksData = fetchTasksData();
        thisRef().append("tasks").setValue(tasksData);
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.itemsComplete" })
    public void updateFooterVisibility() {
        footerVisibility.set(taskRepository.getTasks().size() != 0);
    }

    @ChangeListener(pattern="**.<TaskListModel>.itemsComplete")
    public void updateClearButton() {
        clearButtonVisibility.set(itemsComplete.get() != 0);
        itemsCompleteText.set(String.format("Clear completed (%d)", itemsComplete.get()));
    }

    @ActionListener
    public void newTask(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        itemsLeft.set(itemsLeft.get() + 1);
        toggleAll.set(false);
    }

    @ActionListener
    public void toggleTask(@Param("index") final int index) {
        LOG.info("Completing task {}", index);

        Task task = tasks.get(index);
        if (!task.isCompleted()) {
            task.setCompleted(true);
            itemsLeft.set(itemsLeft.get() - 1);
            itemsComplete.set(itemsComplete.get() + 1);
            toggleAll.set(itemsLeft.get() == 0);
        } else {
            task.setCompleted(false);
            itemsLeft.set(itemsLeft.get() + 1);
            itemsComplete.set(itemsComplete.get() - 1);
            toggleAll.set(false);
        }
        taskRepository.saveTask(task);
    }

    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        LOG.info("Deleting task {}", index);

        Task task = tasks.get(index);
        taskRepository.deleteTask(task);

        itemsLeft.set(taskRepository.getActiveTasks().size());
        itemsComplete.set(taskRepository.getCompletedTasks().size());
    }

    @ActionListener
    public void editTask(@Param("index") final int index, @Param("title") final String title) {
        LOG.info("Editing task {}", index);

        Task task = tasks.get(index);
        task.setTitle(title);
        taskRepository.saveTask(task);
    }

    @ActionListener
    public void toggleAll() {
        //toggleAll.set(!toggleAll.get());

        for (Task t : taskRepository.getTasks()) {
            t.setCompleted(toggleAll.get());
            taskRepository.saveTask(t);
        }
        itemsComplete.set(taskRepository.getCompletedTasks().size());
        itemsLeft.set(taskRepository.getActiveTasks().size());
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clearing completed tasks");

        taskRepository.clearTasks();
        itemsComplete.set(0);
        toggleAll.set(false);
    }

    /*
    // XXX: Not supported. Necessary?
    @ChangeListener(pattern="**.<TaskListModel>.tasks[:index].completed")
    public void completeTodo(int index) {
        LOG.info("completing task ", index);
    }
    */

    private List<Task> fetchTasksData() {
        Filter filterEnum = Filter.valueOf(filter.get());
        return taskRepository.filterTasks(filterEnum);
    }

    public ViewModelProperty<Integer> getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(ViewModelProperty<Integer> itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public ViewModelProperty<String> getFilter() {
        return filter;
    }

    public void setFilter(ViewModelProperty<String> filter) {
        this.filter = filter;
    }

    public ViewModelProperty<Boolean> getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(ViewModelProperty<Boolean> footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public ViewModelProperty<Integer> getItemsComplete() {
        return itemsComplete;
    }

    public void setItemsComplete(ViewModelProperty<Integer> itemsComplete) {
        this.itemsComplete = itemsComplete;
    }

    public ViewModelProperty<String> getItemsCompleteText() {
        return itemsCompleteText;
    }

    public void setItemsCompleteText(ViewModelProperty<String> itemsCompleteText) {
        this.itemsCompleteText = itemsCompleteText;
    }

    public ViewModelProperty<Boolean> getClearButtonVisibility() {
        return clearButtonVisibility;
    }

    public void setClearButtonVisibility(ViewModelProperty<Boolean> clearButtonVisibility) {
        this.clearButtonVisibility = clearButtonVisibility;
    }

    public ViewModelProperty<Boolean> getToggleAll() {
        return toggleAll;
    }

    public void setToggleAll(ViewModelProperty<Boolean> toggleAll) {
        this.toggleAll = toggleAll;
    }

    public ViewModelProperty<Boolean> getFilterAllSelected() {
        return filterAllSelected;
    }

    public void setFilterAllSelected(ViewModelProperty<Boolean> filterAllSelected) {
        this.filterAllSelected = filterAllSelected;
    }

    public ViewModelProperty<Boolean> getFilterActiveSelected() {
        return filterActiveSelected;
    }

    public void setFilterActiveSelected(ViewModelProperty<Boolean> filterActiveSelected) {
        this.filterActiveSelected = filterActiveSelected;
    }

    public ViewModelProperty<Boolean> getFilterCompletedSelected() {
        return filterCompletedSelected;
    }

    public void setFilterCompletedSelected(ViewModelProperty<Boolean> filterCompletedSelected) {
        this.filterCompletedSelected = filterCompletedSelected;
    }
}
