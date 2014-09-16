package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.TaskRepository;

import static at.irian.ankor.viewmodel.factory.BeanFactories.newInstance;

public class ModelRoot {

    private TaskListModel model;

    public ModelRoot(Ref rootRef, TaskRepository taskRepository) {
        this.model = newInstance(TaskListModel.class, rootRef.appendPath("model"), taskRepository);
    }

    public TaskListModel getModel() {
        return model;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }
}
