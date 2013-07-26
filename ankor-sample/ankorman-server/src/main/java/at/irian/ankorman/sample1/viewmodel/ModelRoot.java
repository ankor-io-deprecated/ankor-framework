package at.irian.ankorman.sample1.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.server.AnimalRepository;

/**
 * @author Thomas Spiegl
 */
public class ModelRoot extends ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private String userName;
    private ViewModelProperty<String> serverStatus;
    private Tabs tabs;

    public ModelRoot(Ref viewModelRef, AnimalRepository animalRepository) {
        super(viewModelRef);
        this.tabs = new Tabs(viewModelRef.append("tabs"), animalRepository);
        this.serverStatus.set("");
    }

    @ActionListener
    public void init() {
        //todo ...   how to propagate the root change to the server?!
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
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
