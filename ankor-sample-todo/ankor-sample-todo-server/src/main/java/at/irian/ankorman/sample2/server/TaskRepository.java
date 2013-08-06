package at.irian.ankorman.sample2.server;

import at.irian.ankorman.sample2.domain.task.Task;
import at.irian.ankorman.sample2.viewmodel.animal.helper.Data;
import at.irian.ankorman.sample2.viewmodel.animal.helper.Paginator;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private List<Task> tasks = new ArrayList<Task>();

    public List<Task> getTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks) {
            res.add(new Task(t));
        }
        return res;
    }

    public List<Task> getUncompleteTasks() {
        List<Task> res = new ArrayList<Task>(tasks.size());
        for(Task t : tasks) {
            if (t.isChecked()) {
                res.add(new Task(t));
            }
        }
        return res;
    }

    public List<Task> getCompleteTasks() {
        List<Task> res = getTasks();
        res.removeAll(getUncompleteTasks());
        return res;
    }

    public void saveTask(Task task) {

        // do validation

        int i = 0;
        for (Task t : tasks) {
            if (t.equals(task)) {
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

    public Data<Task> searchTasks(int first, int maxResults) {
        List<Task> animals = getTasks();
        if (first >= animals.size()) {
            return new Data<Task>(new Paginator(animals.size(), maxResults));
        }
        if (first < 0) {
            first = 0;
        }
        int last = first + maxResults;
        if (last > animals.size()) {
            last = animals.size();
        }

        Data<Task> data = new Data<Task>(new Paginator(first, maxResults));

        data.getRows().addAll(animals.subList(first, last));

        // TODO: What is that?
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return data;
    }
}
