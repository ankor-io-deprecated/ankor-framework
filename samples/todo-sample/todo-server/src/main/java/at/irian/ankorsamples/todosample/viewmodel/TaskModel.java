package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankorsamples.todosample.domain.task.Task;

/**
 * Wrapper around the domain task model.
 */
public class TaskModel extends Task {
    // private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskModel.class);

    private boolean editing = false;

    public TaskModel(Task task) {
        super(task);
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TaskModel taskModel = (TaskModel) o;

        return editing == taskModel.editing;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (editing ? 1 : 0);
        return result;
    }
}
