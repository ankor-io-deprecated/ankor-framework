package at.irian.ankorman.sample2.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorman.sample2.viewmodel.task.TaskListModel;

/**
 * Created with IntelliJ IDEA.
 * User: cell303
 * Date: 8/2/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class TabTask extends ViewModelBase {

    private String id;
    private TaskListModel model;
    private ViewModelProperty<String> name;

    public TabTask(String tabId, Ref tabRef, String initialTabName) {
        super(tabRef);
        id = tabId;
        name.set(initialTabName);
    }

    public void close() {
        thisRef().setValue(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setModel(TaskListModel model) {
        this.model = model;
    }

    public TaskListModel getModel() {
        return model;
    }

    public ViewModelProperty<String> getName() {
        return name;
    }

    public void setName(ViewModelProperty<String> name) {
        this.name = name;
    }
}
