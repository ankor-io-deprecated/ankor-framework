package at.irian.ankorsamples.todosample.domain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {

    private static Map<String, Task> tasks = new ConcurrentHashMap<>();

    public Task insertTask(String title, boolean completed) {
        String id = createUniqueId();
        Task task = new Task(id, title, completed);
        tasks.put(id, task);
        return task;
    }

    public void updateTask(Task task) {
        String id = task.getId();
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        tasks.put(id, task.clone());
    }

    private String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    private Task detach(Task task) {
        // simulate detaching by returning a clone
        return task.clone();
    }

    public Task getTaskById(String id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        return detach(task);
    }

    public List<Task> queryTasks(Filter filter) {
        List<Task> resultList = new ArrayList<Task>(tasks.size());
        for (Task t : tasks.values()) {
            if (filter == Filter.all ||
                filter == Filter.active && !t.isCompleted() ||
                filter == Filter.completed && t.isCompleted()) {
                resultList.add(detach(t));
            }
        }

        Collections.sort(resultList);

        return resultList;
    }

    public int countTasks(Filter filter) {
        int count = 0;
        for (Task t : tasks.values()) {
            if (filter == Filter.all ||
                filter == Filter.active && !t.isCompleted() ||
                filter == Filter.completed && t.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public void deleteTask(String taskId) {
        tasks.remove(taskId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }
}
