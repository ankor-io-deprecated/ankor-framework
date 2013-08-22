package at.irian.ankor.todosample.viewmodel.task;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.todosample.domain.task.Task;

import java.util.LinkedHashMap;

public class TaskModel {

    @AnkorIgnore
    private Task task;

    private int index;
    private boolean editing = false;

    public TaskModel(Task task, int index) {
        this.task = task;
        this.index = index;
    }

    public TaskModel(LinkedHashMap<String, Object> task) {
        this.task = new Task();
        this.task.setId((String) task.get("id"));
        this.task.setTitle((String) task.get("title"));
        this.task.setCompleted((Boolean) task.get("completed"));
        this.index = (int) task.get("index");
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
