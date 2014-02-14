package at.irian.ankorsamples.todosample.domain.task;

import at.irian.ankorsamples.todosample.viewmodel.Filter;
import scala.collection.parallel.Tasks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private Map<String, Task> tasks = new LinkedHashMap<>();

    public synchronized void saveTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public synchronized Task findTask(String id) {
        return tasks.get(id);
    }

    public synchronized List<Task> filterTasks(Filter filter) {
        switch (filter) {
            case all:  return getTasks();
            case active: return getActiveTasks();
            case completed: return getCompletedTasks();
        }
        return null;
    }

    public synchronized List<Task> getTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks.values()) {
            res.add(new Task(t));
        }
        return res;
    }

    public synchronized List<Task> getActiveTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks.values()) {
            if (!t.isCompleted()) {
                res.add(new Task(t));
            }
        }
        return res;
    }

    public synchronized List<Task> getCompletedTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks.values()) {
            if (t.isCompleted()) {
                res.add(new Task(t));
            }
        }
        return res;
    }

    public synchronized void clearTasks() {
        Iterator<Map.Entry<String, Task>> it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> next = it.next();
            if (next.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    public synchronized void deleteTask(Task task) {
        tasks.remove(task.getId());
    }
}
