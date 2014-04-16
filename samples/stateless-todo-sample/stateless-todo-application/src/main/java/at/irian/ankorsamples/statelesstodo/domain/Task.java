package at.irian.ankorsamples.statelesstodo.domain;

public class Task implements Comparable<Task>, Cloneable {

    private String id;
    private String title;
    private boolean completed = false;

    /**
     * Just for deserialization
     */
    @SuppressWarnings("UnusedDeclaration")
    public Task() {}

    public Task(String id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (completed != task.completed) return false;
        if (!id.equals(task.id)) return false;
        if (title != null ? !title.equals(task.title) : task.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(Task other) {
        return this.getTitle().compareTo(other.getTitle());
    }

    @Override
    public Task clone() {
        try {
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("clone not supported by super?", e);
        }
    }
}
