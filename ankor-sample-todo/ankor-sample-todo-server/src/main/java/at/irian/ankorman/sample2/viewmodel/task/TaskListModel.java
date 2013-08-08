package at.irian.ankorman.sample2.viewmodel.task;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorman.sample2.domain.task.Task;
import at.irian.ankorman.sample2.server.TaskRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class TaskListModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    @JsonIgnore
    private final TaskRepository taskRepository;

    private List<Task> tasks;

    private ViewModelProperty<String> filter; // XXX: Not possible to use enum type Filter -> type errors again

    // XXX: Maybe annotation to indicate that this property will be initialized by ViewModelBase?
    private ViewModelProperty<Integer> itemsLeft;
    private ViewModelProperty<Boolean> footerVisibility;

    private ViewModelProperty<Integer> itemsComplete;
    private ViewModelProperty<String> itemsCompleteText;
    private ViewModelProperty<Boolean> clearButtonVisibility;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository) {
        super(viewModelRef);

        this.filter.set(Filter.all.toString()); // this.filter = new ViewModelProperty<>(viewModelRef, "filter", Filter.all.toString());

        this.taskRepository = taskRepository;
        this.tasks = fetchTasksData();

        this.itemsLeft.set(taskRepository.getActiveTasks().size());
        this.updateFooterVisibility(); // XXX: Maybe a hook so methods are triggered by set?

        this.itemsComplete.set(taskRepository.getCompletedTasks().size());
        this.updateClearButton();
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.itemsComplete",
            "**.<TaskListModel>.filter"
    })
    public void reloadTasks() {
        LOG.info("reloading tasks");
        List<Task> tasksData = fetchTasksData();
        thisRef().append("tasks").setValue(tasksData);
    }

    @ChangeListener(pattern="**.<TaskListModel>.itemsLeft")
    public void updateFooterVisibility() {
        footerVisibility.set(tasks.size() != 0);
    }

    @ChangeListener(pattern="**.<TaskListModel>.itemsComplete")
    public void updateClearButton() {
        clearButtonVisibility.set(itemsComplete.get() != 0);
        itemsCompleteText.set(String.format("Clear completed (%d)", itemsComplete.get())); // XXX: i18n?
    }

    @ActionListener
    public void newTask(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        // X-X-X: Setting values to refs here causes (sometimes!?) exceptions -> should be fixed in newer ankor commit
        itemsLeft.set(itemsLeft.get() + 1);
    }

    @ActionListener
    public void completeTask(@Param("index") final int index) {
        LOG.info("Completing task {}", index);

        Task task = tasks.get(index);
        if (!task.isCompleted()) {
            task.setCompleted(true);
            itemsLeft.set(itemsLeft.get() - 1);
            itemsComplete.set(itemsComplete.get() + 1);
        } else {
            task.setCompleted(false);
            itemsLeft.set(itemsLeft.get() + 1);
            itemsComplete.set(itemsComplete.get() - 1);
        }
        taskRepository.saveTask(task);
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clearing completed tasks");

        taskRepository.clearTasks();
        itemsComplete.set(0);
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
}
