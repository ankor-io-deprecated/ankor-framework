package at.irian.ankor.core.test;

import at.irian.ankor.core.test.animal.AnimalSearchContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class TestModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private String userName;

    private Map<String, Object> containers = new HashMap<String, Object>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getContainers() {
        return containers;
    }
}
