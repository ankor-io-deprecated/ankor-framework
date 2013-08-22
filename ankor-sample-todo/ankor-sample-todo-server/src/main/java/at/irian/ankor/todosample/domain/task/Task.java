package at.irian.ankor.todosample.domain.task;

import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private boolean completed = false;

    public Task() {}

    public Task(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
    }

    public Task(Task t) {
        this.id = t.id;
        this.title = t.title;
        this.completed = t.completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
