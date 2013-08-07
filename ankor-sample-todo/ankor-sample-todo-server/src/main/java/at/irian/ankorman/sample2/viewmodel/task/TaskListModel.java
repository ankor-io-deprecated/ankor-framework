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

    // XXX: Maybe annotation to indicate that this property will be initialized by ViewModelBase?
    private ViewModelProperty<Integer> itemsLeft;
    private ViewModelProperty<String> filter; // XXX: Not possible to use enum type Filter -> type errors again

    private List<Task> tasks;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository) {
        super(viewModelRef);

        this.taskRepository = taskRepository;

        this.filter.set(Filter.all.toString());
        // this.filter = new ViewModelProperty<>(viewModelRef, "filter", Filter.all.toString());
        this.itemsLeft.set(taskRepository.getTasks().size()); // X-X-X: Weird type error if not using string
        this.tasks = fetchTasksData();
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.filter"
    })
    public void reloadTasks() {
        LOG.info("reloading tasks");
        List<Task> tasksData = fetchTasksData();
        thisRef().append("tasks").setValue(tasksData);
    }

    /*
    // XXX: Not supported. Necessary?
    @ChangeListener(pattern="**.<TaskListModel>.tasks[:index].completed")
    public void completeTodo(int index) {
        LOG.info("completing task ", index);
    }
    */

    @ActionListener(name = "newTodo")
    public void newTodo(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        thisRef().append("itemsLeft").setValue(itemsLeft.get() + 1);

        // X-X-X: Setting values to refs here causes (sometimes!?) exceptions -> should be fixed in newer ankor commit

        // X-X-X: Difference? -> Convenience
        // itemsLeft.set(String.valueOf(currItemsLeft + 1));
    }

    @ActionListener(name = "completeTask")
    public void completeTask(@Param("index") final int index) {
        LOG.info("completing task {}", index);
        Task t = tasks.get(index);
        t.setCompleted(!t.isCompleted());
        taskRepository.saveTask(t);
        reloadTasks();
    }

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
}
