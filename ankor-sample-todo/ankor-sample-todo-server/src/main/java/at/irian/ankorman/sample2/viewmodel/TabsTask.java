// TODO: Remove Tabs
package at.irian.ankorman.sample2.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelMapBase;
import at.irian.ankorman.sample2.server.TaskRepository;
import at.irian.ankorman.sample2.viewmodel.task.TaskListModel;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: cell303
 * Date: 8/2/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TabsTask extends ViewModelMapBase<String, TabTask> {

    private TaskRepository taskRepository;

    protected TabsTask(Ref viewModelRef, TaskRepository taskRepository) {
        super(viewModelRef, new HashMap<String, TabTask>());
        this.taskRepository = taskRepository;
    }

    @Override
    public TabTask put(String key, TabTask value) {
        if (value == null) {
            return map.remove(key);
        } else {
            return map.put(key, value);
        }
    }

    @ActionListener
    public void createTasksTab(@Param("tabId") final String tabId) {
        Ref tabRef = thisRef().append(tabId); // !!!

        TabTask tab = new TabTask(tabId, tabRef, "Tasks");
        TaskListModel model = new TaskListModel(tabRef.append("model"), taskRepository, tab.getName());
        tab.setModel(model); // XXX

        tabRef.setValue(tab);
    }
}
