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

import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends ViewModelBase {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    private enum TasksCategory {
        all,
        uncomplete,
        complete;
    }

    @JsonIgnore
    private final TaskRepository taskRepository;

    //@JsonIgnore
    //private ViewModelProperty<String> tabName;

    private List<Task> tasks = new ArrayList<Task>();

    private ViewModelProperty<String> filter;

    private ViewModelProperty<String> itemsLeft;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository, ViewModelProperty<String> tabName) {
        super(viewModelRef);
        this.taskRepository = taskRepository;
        //this.tabName = tabName;
        this.filter.set(TasksCategory.all.toString());
        this.itemsLeft.set("0");
    }

    @ChangeListener(pattern = "**.<TaskListModel>.filter")
    public void onFilterChanged() {
        // TODO: reload list
    }

    @ActionListener(name = "newTodo")
    public void newTodo(@Param("title") final String title) {
        LOG.info("save action");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        // XXX: fixed weired type bug by changing type from integer to string
        int currItemsLeft = Integer.parseInt(itemsLeft.get());
        itemsLeft.set(String.valueOf(currItemsLeft + 1));
    }

    public ViewModelProperty<String> getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(ViewModelProperty<String> itemsLeft) {
        this.itemsLeft = itemsLeft;
    }
}
