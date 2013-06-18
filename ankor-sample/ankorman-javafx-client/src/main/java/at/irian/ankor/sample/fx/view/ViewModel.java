package at.irian.ankor.sample.fx.view;

/**
 * @author Thomas Spiegl
 */
public class ViewModel {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);
    private String userName;
    private Tabs tabs;

    public ViewModel() {
        tabs = new Tabs();
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

}
