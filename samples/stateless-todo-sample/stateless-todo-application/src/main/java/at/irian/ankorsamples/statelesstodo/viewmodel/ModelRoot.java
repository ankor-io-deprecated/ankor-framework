package at.irian.ankorsamples.statelesstodo.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.statelesstodo.domain.TaskRepository;

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
