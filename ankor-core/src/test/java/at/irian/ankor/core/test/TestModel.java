package at.irian.ankor.core.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class TestModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

    private String userName;
    private Map<String, Object> containers = new HashMap<String, Object>();
    private TestUser testUser;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getContainers() {
        return containers;
    }

    public TestUser getTestUser() {
        return testUser;
    }

    public void setTestUser(TestUser testUser) {
        this.testUser = testUser;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TestModel testModel = (TestModel) o;

        if (!containers.equals(testModel.containers)) { return false; }
        if (testUser != null ? !testUser.equals(testModel.testUser) : testModel.testUser != null) { return false; }
        if (userName != null ? !userName.equals(testModel.userName) : testModel.userName != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + containers.hashCode();
        result = 31 * result + (testUser != null ? testUser.hashCode() : 0);
        return result;
    }
}
