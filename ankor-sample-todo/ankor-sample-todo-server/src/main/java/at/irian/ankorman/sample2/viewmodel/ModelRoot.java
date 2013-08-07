package at.irian.ankorman.sample2.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankorman.sample2.server.TaskRepository;
import at.irian.ankorman.sample2.viewmodel.task.TaskListModel;

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
