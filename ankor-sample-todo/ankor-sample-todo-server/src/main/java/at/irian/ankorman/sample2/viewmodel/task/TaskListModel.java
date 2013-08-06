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

import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends ViewModelBase {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListModel.class);

    private enum Filter {
        all,
        uncomplete,
        complete;
    }

    @JsonIgnore
    private final TaskRepository taskRepository;

    @JsonIgnore
    private ViewModelProperty<String> tabName;

    private List<Task> tasks = new ArrayList<Task>();
    private ViewModelProperty<String> filter;
    private ViewModelProperty<String> itemsLeft;
    private ViewModelProperty<String> itemsCompleted;

    public Data<Task> getAnimals() {
        return animals;
    }

    public void setAnimals(Data<Task> animals) {
        this.animals = animals;
    }

    private Data<Task> animals;

    public TaskListModel(Ref viewModelRef, TaskRepository taskRepository, ViewModelProperty<String> tabName) {
        super(viewModelRef);
        this.tabName = tabName;

        this.taskRepository = taskRepository;
        this.filter.set(Filter.all.toString());
        this.itemsLeft.set("0");
        //this.itemsCompleted.set("Clear completed Tasks (0)");
        this.animals = new Data<Task>(new Paginator(0, 5));
    }

    @ChangeListener(pattern = "**.<TaskListModel>.itemsLeft")
    public void reloadAnimals() {
        // TODO how to load data async and update the animals ref?
        LOG.info("RELOADING animals ...");
        Paginator paginator = animals.getPaginator();
        paginator.reset();
        Data<Task> animals = taskRepository.searchAnimals(paginator.getFirst(), paginator.getMaxResults());

        thisRef().append("animals").setValue(animals);

        LOG.info("... finished RELOADING");
        thisRef().root().append("serverStatus").setValue("");
    }

    @ChangeListener(pattern = "**.<TaskListModel>.tasks")
    public void onFilterChanged() {
        // TODO: reload list
        LOG.info("test");
    }

    /*
    @ChangeListener(pattern = "**.<TaskListModel>.itemsLeft")
    public void onItemsLeftChanged() {
        LOG.info("RELOADING tasks ...");
        tasks = taskRepository.getTasks();
        thisRef().append("tasks").setValue(tasks);
    }
    */

    /*
    @ChangeListener(pattern = {"**.<AnimalSearchModel>.filter.**",
            "**.<AnimalSearchModel>.animals.paginator.**"})
    public void reloadAnimals() {
        LOG.info("RELOADING animals ...");
        Paginator paginator = animals.getPaginator();
        paginator.reset();
        Data<Animal> animals = animalRepository.searchAnimals(filter, paginator.getFirst(), paginator.getMaxResults());

        thisRef().append("animals").setValue(animals);

        LOG.info("... finished RELOADING");
        thisRef().root().append("serverStatus").setValue("");
    }
    */

    @ActionListener(name = "newTodo")
    public void newTodo(@Param("title") final String title) {
        LOG.info("Add new task to task repository");

        Task task = new Task(title);
        taskRepository.saveTask(task);

        // XXX: fixed weired type bug by changing type from integer to string
        //int currItemsLeft = Integer.parseInt(itemsLeft.get());

        //thisRef().append("itemsLeft").setValue(String.valueOf(currItemsLeft + 1));

        // XXX: Why not:
        //itemsLeft.set(String.valueOf(currItemsLeft + 1));
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public ViewModelProperty<String> getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(ViewModelProperty<String> itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public ViewModelProperty<String> getItemsCompleted() {
        return itemsCompleted;
    }

    public void setItemsCompleted(ViewModelProperty<String> itemsCompleted) {
        this.itemsCompleted = itemsCompleted;
    }
}
