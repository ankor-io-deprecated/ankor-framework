package at.irian.ankorman.sample2.server;

import at.irian.ankorman.sample2.domain.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cell303
 * Date: 8/2/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
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
}
