package at.irian.ankor.system;

import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import org.junit.Before;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorSystemTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorSystemTest.class);

    private AnkorSystem system;
    private RefFactory refFactory;
    private MessageFactory messageFactory;
    private RefContext refContext;

    @Before
    public void setup() {
        system = SimpleAnkorSystem.create("Test System", TestModel.class);
        system.start();
        messageFactory = system.getMessageFactory();
        refContext = system.getRefContextFactory().create();
        refFactory = refContext.refFactory();
    }
    
    
//    @Test
//    public void test_remote_init_action() throws Exception {
//        refFactory.rootRef().addActionListener(new ActionListener() {
//            @Override
//            public void process(Ref sourceProperty, Action action) {
//                if (action instanceof SimpleAction && ((SimpleAction) action).getName().equals("init")) {
//                    LOG.info("Creating new MyModel");
//                    Ref root = sourceProperty.root();
//                    root.setValue(new TestModel());
//                    sourceProperty.fireAction(new SimpleAction("initialized"));
//                }
//            }
//        });
//
//        system.getMessageBus().receiveMessage(messageFactory.createActionMessage(null, refFactory.rootRef().path(), new SimpleAction("init")));
//
//        Object model = refContext.getModelHolder().getModel();
//        Assert.assertNotNull(model);
//    }


//    @Test
//    public void test_remote_change() throws Exception {
//
//        TestModel model = new TestModel();
//        application.getModelHolder().setModel(model);
//
//        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.userName"), new ChangeListener() {
//            @Override
//            public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
//                watchedProperty.setValue("Max Muster 2");
//            }
//        });
//
//        server.receiveChange("root.userName", "Max Muster");
//
//        Assert.assertEquals("Max Muster 2", model.getUserName());
//    }
//
//    @Test
//    public void test_remote_change_typed() throws Exception {
//
//        TestModel model = new TestModel();
//        application.getModelHolder().setModel(model);
//
//        listenerRegistry.registerRemoteChangeListener(refFactory.ref("root.testUser"), new ChangeListener() {
//            @Override
//            public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
//                LOG.info("watched = {}", watchedProperty);
//                LOG.info("changed = {}", changedProperty);
//            }
//        });
//
//        TestUser newValue = new TestUser("Max", "Muster");
//        server.receiveChange("root.testUser", newValue);
//
//        Assert.assertEquals(newValue, model.getTestUser());
//    }
//
//    @Test
//    public void test_local_change_and_action() throws Exception {
//
//        TestModel model = new TestModel();
//        application.getModelHolder().setModel(model);
//
//        listenerRegistry.registerRemoteActionListener(refFactory.ref("root.userName"), new at.irian.ankor.event.ActionListener() {
//            @Override
//            public void process(Ref source, ActionEvent actionEvent) {
//                if (actionEvent.name().equals("loadUser")) {
//                    String userName = "Max Muster";
//                    source.setValue(userName);
//                    source.fire(SimpleAction.create("success"));
//                }
//            }
//        });
//        server.receiveAction("root.userName", SimpleAction.create("loadUser"));
//
//        Assert.assertEquals("Max Muster", model.getUserName());
//    }
//
//
//    @Test
//    public void test_context() throws Exception {
//        TestModel model = new TestModel();
//        model.setTestUser(new TestUser("Max", "Muster"));
//        application.getModelHolder().setModel(model);
//
//        Ref contextRef = refFactory.ref("root.testUser");
//        Ref ref = refFactory.ref("context.firstName").withModelContext(contextRef);
//        Assert.assertEquals("Max", ref.getValue());
//    }
//
//
    public static class TestModel {
        //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyModel.class);

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
