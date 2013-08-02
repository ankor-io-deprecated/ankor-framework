package at.irian.ankorman.sample2.viewmodel.task;

import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorman.sample2.domain.task.Task;
import at.irian.ankorman.sample2.server.TaskRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cell303
 * Date: 8/2/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskListModel extends ViewModelBase {

    private enum TasksCategory {
        all,
        uncomplete,
        complete;
    }

    @JsonIgnore
    private final TaskRepository taskRepository;

    @JsonIgnore
    private ViewModelProperty<String> tabName;

    private List<Task> tasks = new ArrayList<Task>();

    private ViewModelProperty<String> tasksCategory;
    private ViewModelProperty<Integer> itemsLeft;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository, ViewModelProperty<String> tabName) {
        super(viewModelRef);
        this.taskRepository = taskRepository;
        this.tabName = tabName;
        this.tasksCategory.set(TasksCategory.all.toString());
        this.itemsLeft.set(0);
    }

    @ChangeListener(pattern = "**.<TaskListModel>.tasksCategory")
    public void onCategoryChanged() {
        // TODO: reload list
    }
}
