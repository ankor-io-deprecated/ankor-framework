package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class ModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private ViewModelProperty<String> userName;
    private ViewModelProperty<String> serverStatus;
    private Tabs tabs;

    public ModelRoot(Ref viewModelRef, AnimalRepository animalRepository) {
        AnkorPatterns.initViewModel(this, viewModelRef);
        this.userName.set("");
        this.serverStatus.set("");
        this.tabs = new Tabs(viewModelRef.appendPath("tabs"), animalRepository);
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
