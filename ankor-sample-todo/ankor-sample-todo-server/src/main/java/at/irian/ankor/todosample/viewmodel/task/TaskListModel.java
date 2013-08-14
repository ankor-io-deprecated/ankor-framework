package at.irian.ankor.todosample.viewmodel.task;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.todosample.domain.task.Task;
import at.irian.ankor.todosample.server.TaskRepository;
import at.irian.ankor.viewmodel.ViewModelBase;

import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    @AnkorIgnore
    private final TaskRepository taskRepository;

    private List<Task> tasks;

    @AnkorIgnore
    private String filter;

    private Integer itemsLeft;
    private Boolean footerVisibility;

    @AnkorIgnore
    private Integer itemsComplete;
    private String itemsCompleteText;

    private Boolean clearButtonVisibility;

    private Boolean toggleAll;

    private Boolean filterAllSelected;
    private Boolean filterActiveSelected;
    private Boolean filterCompletedSelected;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository) {
        super(viewModelRef);

        this.taskRepository = taskRepository;

        filter = Filter.all.toString();
        filterAllSelected = (true);
        filterActiveSelected = (false);
        filterCompletedSelected = (false);

        tasks = new ArrayList<Task>(fetchTasksData());

        itemsLeft = taskRepository.getActiveTasks().size();
        footerVisibility = taskRepository.getTasks().size() != 0;

        itemsComplete = (taskRepository.getCompletedTasks().size());
        clearButtonVisibility = (itemsComplete != 0);
        itemsCompleteText = (String.format("Clear completed (%d)", itemsComplete));

        toggleAll = (false);

        /*
        RefListeners.addTreeChangeListener(tasksRef(), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                if (changedProperty.propertyName().equals("completed")) {
                    Task task = changedProperty.parent().getValue();
                    int index = tasks.indexOf(task);

                    LOG.info("Completing task {}", index);

                    boolean isCompleted = changedProperty.getValue();
                    if (isCompleted) {
                        thisRef("itemsLeft").setValue(itemsLeft - 1);
                        thisRef("itemsComplete").setValue(itemsComplete + 1);
                        thisRef("toggleAll").setValue(itemsLeft == 0);
                    } else {
                        thisRef("itemsLeft").setValue(itemsLeft + 1);
                        thisRef("itemsComplete").setValue(itemsComplete - 1);
                        thisRef("toggleAll").setValue(false);
                    }
                    TaskListModel.this.taskRepository.saveTask(task);
                }
            }
        });
        */
    }

    private Ref tasksRef() {
        return thisRef("tasks");
    }

    private Ref tasksRef(int index) {
        return thisRef("tasks").appendIdx(index);
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.filterAllSelected",
            "**.<TaskListModel>.filterActiveSelected",
            "**.<TaskListModel>.filterCompletedSelected" })
    public void reloadTasks() {
        LOG.info("reloading tasks");

        if (filterAllSelected) {
            filter = Filter.all.toString();
        } else if (filterActiveSelected) {
            filter = Filter.active.toString();
        } else if (filterCompletedSelected) {
            filter = Filter.completed.toString();
        }

        tasksRef().setValue(fetchTasksData());
    }
    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.itemsComplete" })
    public void updateFooterVisibility() {
        thisRef("footerVisibility").setValue(taskRepository.getTasks().size() != 0);
    }

    @ChangeListener(pattern="**.<TaskListModel>.itemsComplete")
    public void updateClearButton() {
        thisRef("clearButtonVisibility").setValue(itemsComplete != 0);
        thisRef("itemsCompleteText").setValue(String.format("Clear completed (%d)", itemsComplete));
    }

    @ActionListener
    public void newTask(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        tasksRef().setValue(fetchTasksData());

        thisRef("itemsLeft").setValue(itemsLeft + 1);
        thisRef("toggleAll").setValue(false);
    }

    @ActionListener
    public void toggleTask(@Param("index") final int index) {
        LOG.info("Completing task {}", index);

        Task task = tasks.get(index);
        if (!task.isCompleted()) {
            task.setCompleted(true);
            //tasksRef(index).append("completed").setValue(true);
            thisRef("itemsLeft").setValue(itemsLeft - 1);
            thisRef("itemsComplete").setValue(itemsComplete + 1);
            thisRef("toggleAll").setValue(itemsLeft == 0);
        } else {
            task.setCompleted(false);
            //tasksRef(index).append("completed").setValue(false);
            thisRef("itemsLeft").setValue(itemsLeft + 1);
            thisRef("itemsComplete").setValue(itemsComplete - 1);
            thisRef("toggleAll").setValue(false);
        }

        taskRepository.saveTask(task);
        tasksRef().setValue(fetchTasksData());
    }

    @ActionListener
    public void deleteTask(@Param("index") final int index) {
        LOG.info("Deleting task {}", index);

        Task task = tasks.get(index);
        taskRepository.deleteTask(task);

        tasksRef().setValue(fetchTasksData());

        thisRef("itemsLeft").setValue(taskRepository.getActiveTasks().size());
        thisRef("itemsComplete").setValue(taskRepository.getCompletedTasks().size());
    }

    @ActionListener
    public void editTask(@Param("index") final int index, @Param("title") final String title) {
        LOG.info("Editing task {}", index);

        Task task = tasks.get(index);
        // XXX: It's really easy to forget the append("title") here.
        tasksRef(index).append("title").setValue(title);
        taskRepository.saveTask(task);
    }

    @ActionListener
    public void toggleAll() {
        int i = 0;
        for (Task t : taskRepository.getTasks()) {
            t.setCompleted(toggleAll);
            taskRepository.saveTask(t);
        }
        tasksRef().setValue(fetchTasksData());
        thisRef("itemsComplete").setValue(taskRepository.getCompletedTasks().size());
        thisRef("itemsLeft").setValue(taskRepository.getActiveTasks().size());
    }

    @ActionListener
    public void clearTasks() {
        LOG.info("Clearing completed tasks");

        taskRepository.clearTasks();
        tasksRef().setValue(fetchTasksData());
        thisRef("itemsComplete").setValue(0);
        thisRef("toggleAll").setValue(false);
    }

    /*
    // XXX: Not supported. Necessary?
    @ChangeListener(pattern="**.<TaskListModel>.tasks[(*)].completed")
    public void completeTodo(int index) {
        LOG.info("completing task ", index);
    }
    */

    private List<Task> fetchTasksData() {
        Filter filterEnum = Filter.valueOf(filter);
        return taskRepository.filterTasks(filterEnum);
    }

    public Integer getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(Integer itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
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
}
