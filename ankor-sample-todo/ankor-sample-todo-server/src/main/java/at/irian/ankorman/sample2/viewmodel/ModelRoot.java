package at.irian.ankorman.sample2.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorman.sample2.server.AnimalRepository;
import at.irian.ankorman.sample2.server.TaskRepository;

/**
 * @author Thomas Spiegl
 */
//@SuppressWarnings("UnusedDeclaration")
public class ModelRoot extends ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private ViewModelProperty<String> userName;
    private ViewModelProperty<String> serverStatus;
    private Tabs tabs;

    public TabsTask getTabsTask() {
        return tabsTask;
    }

    public void setTabsTask(TabsTask tabsTask) {
        this.tabsTask = tabsTask;
    }

    public void setTabs(Tabs tabs) {
        this.tabs = tabs;
    }

    private TabsTask tabsTask;

    public ModelRoot(Ref viewModelRef, AnimalRepository animalRepository, TaskRepository taskRepository) { // XXX
        super(viewModelRef);
        this.tabs = new Tabs(viewModelRef.append("tabs"), animalRepository);
        this.userName.set("");
        this.serverStatus.set("");

        this.tabsTask = new TabsTask(viewModelRef.append("tabsTask"), taskRepository); // XXX
    }

    @ActionListener
    public void init() {
        userName.set("John Doe");
    }

    public ViewModelProperty<String> getUserName() {
        return userName;
    }

    public void setUserName(ViewModelProperty<String> userName) {
        this.userName = userName;
    }

    public Tabs getTabs() {
        return tabs;
    }

    public ViewModelProperty<String> getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(ViewModelProperty<String> serverStatus) {
        this.serverStatus = serverStatus;
    }
}
