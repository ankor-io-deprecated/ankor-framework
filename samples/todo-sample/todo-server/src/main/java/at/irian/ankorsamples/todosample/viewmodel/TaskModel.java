package at.irian.ankorsamples.todosample.viewmodel;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankorsamples.todosample.domain.task.Task;

import java.util.LinkedHashMap;

/**
 * Wrapper around the domain task model.
 */
public class TaskModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskModel.class);

    @AnkorIgnore
    private Task task;

    private boolean editing = false;

    public TaskModel(Task task) {
        this.task = task;
    }

    public TaskModel(LinkedHashMap<String, Object> task) {
        this.task = new Task();
        this.task.setId((String) task.get("id"));
        this.task.setTitle((String) task.get("title"));
        this.task.setCompleted((Boolean) task.get("completed"));
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getId() {
        return task.getId();
    }

    public void setId(String id) {
        this.task.setId(id);
    }

    public String getTitle() {
        return task.getTitle();
    }

    public void setTitle(String title) {
        task.setTitle(title);
    }

    public boolean isCompleted() {
        return task.isCompleted();
    }

    public void setCompleted(boolean completed) {
        task.setCompleted(completed);
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

        TaskModel taskModel = (TaskModel) o;

        if (editing != taskModel.editing) return false;
        if (task != null ? !task.equals(taskModel.task) : taskModel.task != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = task != null ? task.hashCode() : 0;
        result = 31 * result + (editing ? 1 : 0);
        return result;
    }
}
