package at.irian.ankorman.sample2.domain.task;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: cell303
 * Date: 8/2/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class Task {
    private String id;
    private String title = "";
    private boolean checked = false;

    public Task(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
    }

    public Task(Task t) {
        this.id = t.id;
        this.title = t.title;
        this.checked = t.checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (!id.equals(task.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
