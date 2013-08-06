package at.irian.ankorman.sample2.viewmodel.task;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorman.sample2.domain.task.Task;
import at.irian.ankorman.sample2.server.TaskRepository;
import at.irian.ankorman.sample2.viewmodel.animal.helper.Data;
import at.irian.ankorman.sample2.viewmodel.animal.helper.Paginator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TaskListModel extends ViewModelBase {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    @JsonIgnore
    private final TaskRepository taskRepository;

    @JsonIgnore
    private ViewModelProperty<String> tabName;

    private ViewModelProperty<String> filter;
    private ViewModelProperty<String> itemsLeft;

    // TODO: Use a list instead of Data<Task>
    private Data<Task> tasks;

    // XXX: Did not work using a list
    //private List<Task> tasks = new ArrayList<Task>();

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository, ViewModelProperty<String> tabName) {
        super(viewModelRef);
        this.tabName = tabName;

        this.taskRepository = taskRepository;

        this.filter.set(Filter.all.toString());
        this.itemsLeft.set(String.valueOf(taskRepository.getTasks().size())); // XXX: Weird type error if not using string
        this.tasks = fetchTasksData(new Paginator(0, Integer.MAX_VALUE));
    }

    @ChangeListener(pattern = {
            "**.<TaskListModel>.itemsLeft",
            "**.<TaskListModel>.filter"
    })
    public void reloadTasks() {
        LOG.info("reloading tasks");
        Data<Task> tasksData = fetchTasksData(tasks.getPaginator());
        thisRef().append("tasks").setValue(tasksData);
    }

    @ActionListener(name = "newTodo")
    public void newTodo(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        // XXX: Setting values to refs here causes (sometimes!?) exceptions

        //int currItemsLeft = Integer.parseInt(itemsLeft.get());
        //thisRef().append("itemsLeft").setValue(String.valueOf(currItemsLeft + 1));

        // XXX: Difference?
        //itemsLeft.set(String.valueOf(currItemsLeft + 1));
    }

    private Data<Task> fetchTasksData(Paginator paginator) {
        paginator.reset();
        Filter filterEnum = Filter.valueOf(filter.get());
        return taskRepository.searchTasks(filterEnum, paginator.getFirst(), paginator.getMaxResults());
    }

    public ViewModelProperty<String> getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(ViewModelProperty<String> itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public Data<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Data<Task> tasks) {
        this.tasks = tasks;
    }

    public ViewModelProperty<String> getFilter() {
        return filter;
    }

    public void setFilter(ViewModelProperty<String> filter) {
        this.filter = filter;
    }

}
