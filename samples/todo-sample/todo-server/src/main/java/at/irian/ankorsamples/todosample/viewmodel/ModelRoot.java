package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;

public class ModelRoot {

    private TaskListModel model;

    public ModelRoot(Ref rootRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, rootRef);
        model = new TaskListModel(rootRef.appendPath("model"), taskRepository);
    }

    public TaskListModel getModel() {
        return model;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }
}
