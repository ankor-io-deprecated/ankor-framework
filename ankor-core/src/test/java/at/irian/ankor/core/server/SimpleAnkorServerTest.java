package at.irian.ankor.core.server;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.application.DefaultApplication;
import at.irian.ankor.core.application.SimpleApplication;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.ref.RefFactory;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorServerTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorServerTest.class);

    private DefaultApplication application;
    private SimpleAnkorServer server;
    private RefFactory refFactory;
    private ListenerRegistry listenerRegistry;

    @Before
    public void setup() {
        application = SimpleApplication.create(TestModel.class);
        server = new SimpleAnkorServer(application, "server");
        refFactory = application.getRefFactory();
        listenerRegistry = application.getListenerRegistry();
    }
    
    
    @Test
    public void test_remote_init_action() throws Exception {
        listenerRegistry.registerRemoteActionListener(refFactory.rootRef(), new ModelActionListener() {
            @Override
            public void handleModelAction(Ref actionContext, ModelAction action) {
                if (action.name().equals("init")) {
                    LOG.info("Creating new TestModel");
                    Ref root = actionContext.root();
                    root.setValue(new TestModel());
                    actionContext.fire(SimpleAction.create("initialized"));
                }
            }
        });
        server.receiveAction((String) null, SimpleAction.create("init"));

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }


    @Test
    public void test_remote_change() throws Exception {

        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);

        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.userName"), new ModelChangeListener() {
            @Override
            public void handleModelChange(Ref watchedRef, Ref changedRef) {
                watchedRef.setValue("Max Muster 2");
            }
        });

        server.receiveChange("root.userName", "Max Muster");

        Assert.assertEquals("Max Muster 2", model.getUserName());
    }

    @Test
    public void test_remote_change_typed() throws Exception {

        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);

        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.testUser"), new ModelChangeListener() {
            @Override
            public void handleModelChange(Ref watchedRef, Ref changedRef) {
                LOG.info("watched = {}", watchedRef);
                LOG.info("changed = {}", changedRef);
            }
        });

        TestUser newValue = new TestUser("Max", "Muster");
        server.receiveChange("root.testUser", newValue);

        Assert.assertEquals(newValue, model.getTestUser());
    }

    @Test
    public void test_local_change_and_action() throws Exception {

        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);

        listenerRegistry.registerRemoteActionListener(refFactory.ref("root.userName"), new ModelActionListener() {
            @Override
            public void handleModelAction(Ref actionContext, ModelAction action) {
                if (action.name().equals("loadUser")) {
                    String userName = "Max Muster";
                    actionContext.setValue(userName);
                    actionContext.fire(SimpleAction.create("success"));
                }
            }
        });
        server.receiveAction("root.userName", SimpleAction.create("loadUser"));

        Assert.assertEquals("Max Muster", model.getUserName());
    }





    public static class TestModel {
        //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestModel.class);

        private String userName;
        private TestUser testUser;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public TestUser getTestUser() {
            return testUser;
        }

        public void setTestUser(TestUser testUser) {
            this.testUser = testUser;
        }

    }

    public static class TestUser {
        //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestUser.class);

        private final String firstName;
        private final String lastName;

        public TestUser(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return "TestUser{" +
                   "firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   '}';
        }
    }


}
