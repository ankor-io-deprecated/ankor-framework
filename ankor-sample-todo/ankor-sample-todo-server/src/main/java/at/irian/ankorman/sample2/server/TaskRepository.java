package at.irian.ankorman.sample2.server;

import at.irian.ankorman.sample2.domain.task.Task;
import at.irian.ankorman.sample2.viewmodel.task.Filter;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private List<Task> tasks = new ArrayList<Task>();

    public void saveTask(Task task) {

        // do validation

        int i = 0;
        for (Task t : tasks) {
            if (t.getId().equals(task.getId())) {
                tasks.set(i, new Task(task));
                return;
            }
            i++;
        }

        tasks.add(new Task(task));
    }

    public Task findTask(String id) {
        for (Task t : tasks) {
            if (t.getId().equals(id)) {
                return new Task(t);
            }
        }
        return null;
    }

    public List<Task> filterTasks(Filter filter) {
        switch (filter) {
            case all:  return getTasks();
            case active: return getActiveTasks();
            case completed: return getCompletedTasks();
        }
        return null;
    }

    public List<Task> getTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks) {
            res.add(new Task(t));
        }
        return res;
    }

    public List<Task> getActiveTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks) {
            if (!t.isCompleted()) {
                res.add(new Task(t));
            }
        }
        return res;
    }

    public List<Task> getCompletedTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks) {
            if (t.isCompleted()) {
                res.add(new Task(t));
            }
        }
        return res;
    }

    public void clearTasks() {
        tasks = getActiveTasks();
    }
}
