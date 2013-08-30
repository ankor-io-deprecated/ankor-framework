package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.task.TaskListModel;

public class ModelRoot extends ViewModelBase {

    private TaskListModel model;

    public ModelRoot(Ref rootRef, TaskRepository taskRepository) {
        super(rootRef);
        model = new TaskListModel(rootRef.appendPath("model"), taskRepository);
    }

    public TaskListModel getModel() {
        return model;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }
}
