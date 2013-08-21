package at.irian.ankor.todosample.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.todosample.server.TaskRepository;
import at.irian.ankor.todosample.viewmodel.task.TaskListModel;
import at.irian.ankor.viewmodel.ViewModelBase;

public class ModelRoot extends ViewModelBase {

    private TaskListModel model;

    public ModelRoot(Ref rootRef, TaskRepository taskRepository) {
        super(rootRef);
        model = new TaskListModel(rootRef.append("model"), taskRepository);
    }

    public TaskListModel getModel() {
        return model;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }
}
