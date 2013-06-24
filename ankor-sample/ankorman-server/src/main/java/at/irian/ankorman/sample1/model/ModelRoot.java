package at.irian.ankorman.sample1.model;

/**
 * @author Thomas Spiegl
 */
public class ModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private String userName;
    private String serverStatus;
    private Tabs tabs;

    public ModelRoot() {
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

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }
}
