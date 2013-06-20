package at.irian.ankor.service;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.DefaultApplication;
import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.action.ActionListener;
import at.irian.ankor.change.ChangeListener;
import at.irian.ankor.application.ListenerRegistry;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
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
        listenerRegistry.registerRemoteActionListener(refFactory.rootRef(), new ActionListener() {
            @Override
            public void processAction(Ref actionContext, Action action) {
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

        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.userName"), new ChangeListener() {
            @Override
            public void processChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
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

        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.testUser"), new ChangeListener() {
            @Override
            public void processChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
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

        listenerRegistry.registerRemoteActionListener(refFactory.ref("root.userName"), new ActionListener() {
            @Override
            public void processAction(Ref actionContext, Action action) {
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


    @Test
    public void test_context() throws Exception {
        TestModel model = new TestModel();
        model.setTestUser(new TestUser("Max", "Muster"));
        application.getModelHolder().setModel(model);

        Ref contextRef = refFactory.ref("root.testUser");
        Ref ref = refFactory.ref("context.firstName").withModelContext(contextRef);
        Assert.assertEquals("Max", ref.getValue());
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

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
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
