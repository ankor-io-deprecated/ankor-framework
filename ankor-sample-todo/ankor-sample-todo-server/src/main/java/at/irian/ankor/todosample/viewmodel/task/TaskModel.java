package at.irian.ankor.todosample.viewmodel.task;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.todosample.domain.task.Task;

import java.util.LinkedHashMap;

/**
 * Wrapper around the domain task model.
 */
public class TaskModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskModel.class);

    @AnkorIgnore
    private String id;

    @AnkorIgnore
    private Task task;

    private boolean editing = false;

    public TaskModel(Task task) {
        this.task = task;
        this.id = task.getId();
    }

    public TaskModel(LinkedHashMap<String, Object> task) {
        this.task = new Task();
        this.task.setId((String) task.get("id"));
        this.task.setTitle((String) task.get("title"));
        this.task.setCompleted((Boolean) task.get("completed"));

        this.id = (String) task.get("id");
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    /*
     * XXX
     * This is working for now but needs to be reviewed.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskModel taskModel = (TaskModel) o;

        if (!id.equals(taskModel.id)) return false;
        if (!task.isCompleted() == taskModel.isCompleted()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
