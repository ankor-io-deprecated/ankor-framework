package at.irian.ankor.core.server;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.test.*;
import at.irian.ankor.core.test.animal.AnimalSearchActionListener;
import at.irian.ankor.core.test.NewContainerActionListener;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorServerTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerTest.class);

    @Test
    public void test_remote_init_action() throws Exception {

        Application application = new Application(TestModel.class);
        AnkorServer server = new AnkorServer(application);

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().rootRef(),
                                                                       new InitActionListener());

        server.handleClientAction(null, "init");

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }


    @Test
    public void test_remote_change() throws Exception {

        Application application = new Application(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        AnkorServer server = new AnkorServer(application);

        application.getListenerRegistry().registerRemoteChangeListener(application.getRefFactory().ref("userName"),
                                                                       new UserNameChangeListener());

        server.handleClientChange("userName", "Max Muster");

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }

    @Test
    public void test_local_change_and_action() throws Exception {

        Application application = new Application(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        AnkorServer server = new AnkorServer(application);

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().ref("userName"),
                                                                       new LoadUserActionListener());
        server.handleClientAction("userName", "loadUser");

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }


    @Test
    public void test_animal_search() throws Exception {

        Application application = new Application(TestModel.class);
        AnkorServer server = new AnkorServer(application);

        application.getListenerRegistry().registerRemoteActionListener(null, new InitActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        server.handleClientAction(null, "init");

        server.handleClientChange("containers['tab1']", null);
        server.handleClientAction("containers['tab1']", "newAnimalSearchContainer");

        server.handleClientChange("containers['tab1'].filter.name", "A*");
        server.handleClientChange("containers['tab1'].filter.type", "Bird");
        server.handleClientAction("containers['tab1']", "search");

    }


}
