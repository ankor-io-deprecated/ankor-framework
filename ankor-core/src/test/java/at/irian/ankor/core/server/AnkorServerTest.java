package at.irian.ankor.core.server;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.test.*;
import at.irian.ankor.core.test.animal.AnimalSearchActionListener;
import at.irian.ankor.core.test.NewContainerActionListener;
import at.irian.ankor.core.test.animal.AnimalType;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorServerTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerTest.class);

    @Test
    public void test_remote_init_action() throws Exception {

        Application application = new Application(TestModel.class);
        AnkorServer server = new AnkorServer("server", application);

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().rootRef(),
                                                                       new InitActionListener());

        server.handleRemoteAction(null, "init");

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }


    @Test
    public void test_remote_change() throws Exception {

        Application application = new Application(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        AnkorServer server = new AnkorServer("server", application);

        application.getListenerRegistry().registerRemoteChangeListener(application.getRefFactory().ref("userName"),
                                                                       new UserNameChangeListener());

        server.handleRemoteChange("userName", "Max Muster");

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }

    @Test
    public void test_remote_change_typed() throws Exception {

        Application application = new Application(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        AnkorServer server = new AnkorServer("server", application);

        application.getListenerRegistry().registerRemoteChangeListener(application.getRefFactory().ref("testUser"),
                                                                       new ModelChangeListener() {
                                                                           @Override
                                                                           public void beforeModelChange(ModelRef modelRef,
                                                                                                         Object oldValue,
                                                                                                         Object newValue) {
                                                                           }

                                                                           @Override
                                                                           public void afterModelChange(ModelRef modelRef,
                                                                                                        Object oldValue,
                                                                                                        Object newValue) {
                                                                               LOG.info("change from client {}, {}, {}", modelRef, oldValue, newValue);
                                                                           }
                                                                       });

        server.handleRemoteChange("testUser", new TestUser("Max", "Muster"));

        //Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }

    @Test
    public void test_local_change_and_action() throws Exception {

        Application application = new Application(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        AnkorServer server = new AnkorServer("server", application);

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().ref("userName"),
                                                                       new LoadUserActionListener());
        server.handleRemoteAction("userName", "loadUser");

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }


    @Test
    public void test_animal_search() throws Exception {

        Application application = new Application(TestModel.class);
        AnkorServer server = new AnkorServer("server", application);

        application.getListenerRegistry().registerRemoteActionListener(null, new InitActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        server.handleRemoteAction(null, "init");

        server.handleRemoteChange("containers['tab1']", null);
        server.handleRemoteAction("containers['tab1']", "newAnimalSearchContainer");

        server.handleRemoteChange("containers['tab1'].filter.name", "A*");
        server.handleRemoteChange("containers['tab1'].filter.type", "Bird");
        server.handleRemoteAction("containers['tab1']", "search");

    }


    @Test
    public void test_animal_search_with_mock_client() throws Exception {

        final Application serverApp = new Application(TestModel.class);
        AnkorServer server = new AnkorServer("server", serverApp);
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new ModelActionListener() {
            @Override
            public void handleModelAction(ModelRef modelRef, String action) {
                if (action.equals("init")) {
                    LOG.info("Creating new TestModel");
                    modelRef.root().setValue(new TestModel());
                    modelRef.fireAction("initialized");
                }
            }
        });
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        final Application clientApp = new Application(Object.class);
        AnkorServer client = new AnkorServer("client", clientApp);
        clientApp.getListenerRegistry().registerRemoteActionListener(null, new ModelActionListener() {
            @Override
            public void handleModelAction(ModelRef modelRef, String action) {
                if (action.equals("initialized")) {
                    ModelRef containerRef = modelRef.root().with("containers['tab1']");
                    containerRef.setValue(null);

                    clientApp.getListenerRegistry().registerRemoteChangeListener(containerRef, new ModelChangeListener() {
                        @Override
                        public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
                        }

                        @Override
                        public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
                            LOG.info("new container {}", newValue);

                            modelRef.with("filter.name").setValue("A*");
                            modelRef.with("filter.type").setValue(AnimalType.Bird);
                            modelRef.fireAction("search");
                        }
                    });

                    containerRef.fireAction("newAnimalSearchContainer");
                }
            }
        });

        server.setRemoteServer(client);
        client.setRemoteServer(server);

        server.handleRemoteAction(null, "init");

//        server.handleRemoteChange("containers['tab1']", null);
//        server.handleRemoteAction("containers['tab1']", "newAnimalSearchContainer");

//        server.handleRemoteChange("containers['tab1'].filter.name", "A*");
//        server.handleRemoteChange("containers['tab1'].filter.type", "Bird");
//        server.handleRemoteAction("containers['tab1']", "search");

    }


}
