package at.irian.ankor.core.server;

import at.irian.ankor.core.action.method.RemoteMethodAction;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.application.ModelHolder;
import at.irian.ankor.core.application.SimpleApplication;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.test.*;
import at.irian.ankor.core.test.animal.*;
import at.irian.ankor.core.test.NewContainerActionListener;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorServerTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorServerTest.class);

    @Test
    public void test_remote_init_action() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().rootRef(),
                                                                       new InitActionListener());

        server.receiveAction((String) null, SimpleAction.create("init"));

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }


    @Test
    public void test_remote_change() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteChangeListener(application.getRefFactory().ref("userName"),
                                                                       new UserNameChangeListener());

        server.receiveChange("userName", "Max Muster");

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }

    @Test
    public void test_remote_change_typed() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteChangeListener(application.getRefFactory().ref("testUser"),
                                                                       new ModelChangeListener() {

                                                                           @Override
                                                                           public void handleModelChange(Ref watchedRef,
                                                                                                         Ref changedRef) {
                                                                               LOG.info("change from client {}, {}",
                                                                                        watchedRef, watchedRef.getValue());
                                                                           }
                                                                       });

        server.receiveChange("testUser", new TestUser("Max", "Muster"));

        //Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }

    @Test
    public void test_local_change_and_action() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class);
        TestModel model = new TestModel();
        application.getModelHolder().setModel(model);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteActionListener(application.getRefFactory().ref("userName"),
                                                                       new LoadUserActionListener());
        server.receiveAction("userName", SimpleAction.create("loadUser"));

        Assert.assertEquals("user name", "Max Muster", model.getUserName());
    }


    @Test
    public void test_animal_search() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteActionListener(null, new InitActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        server.receiveAction((String) null, SimpleAction.create("init"));

        server.receiveChange("containers['tab1']", null);
        server.receiveAction("containers['tab1']", SimpleAction.create("newAnimalSearchContainer"));

        server.receiveChange("containers['tab1'].filter.name", "A*");
        server.receiveChange("containers['tab1'].filter.type", "Bird");
        server.receiveAction("containers['tab1']", SimpleAction.create("search"));

    }


    @Test
    public void test_animal_search_with_mock_client() throws Exception {

        final Application serverApp = SimpleApplication.withModelType(TestModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(serverApp, "server");
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new ModelActionListener() {
            @Override
            public void handleModelAction(Ref actionContext, ModelAction action) {
                if (action.name().equals("init")) {
                    LOG.info("Creating new TestModel");
                    actionContext.root().setValue(new TestModel());
                    actionContext.fire(SimpleAction.create("initialized"));
                }
            }
        });
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        final Application clientApp = SimpleApplication.withModelType(TestModel.class);
        SimpleAnkorServer client = new SimpleAnkorServer(clientApp, "client");
        clientApp.getListenerRegistry().registerRemoteActionListener(null, new ModelActionListener() {
            @Override
            public void handleModelAction(Ref actionContext, ModelAction action) {
                if (action.name().equals("initialized")) {
                    Ref containerRef = actionContext.root().sub("containers['tab1']");
                    containerRef.setValue(null);

                    clientApp.getListenerRegistry().registerRemoteChangeListener(containerRef, new ModelChangeListener() {

                        @Override
                        public void handleModelChange(Ref watchedRef, Ref changedRef) {
                            LOG.info("new container {}", watchedRef.getValue());

                            watchedRef.sub("filter.name").setValue("A*");
                            watchedRef.sub("filter.type").setValue(AnimalType.Bird);
                            watchedRef.fire(SimpleAction.create("search"));
                        }
                    });

                    containerRef.fire(SimpleAction.create("newAnimalSearchContainer"));
                }
            }
        });

        server.setRemoteServer(client);
        client.setRemoteServer(server);

        server.receiveAction((String) null, SimpleAction.create("init"));

//        server.receiveChange("containers['tab1']", null);
//        server.receiveAction("containers['tab1']", "newAnimalSearchContainer");

//        server.receiveChange("containers['tab1'].filter.name", "A*");
//        server.receiveChange("containers['tab1'].filter.type", "Bird");
//        server.receiveAction("containers['tab1']", "search");

    }


    @Test
    public void test_method_action() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class)
                                                   .withBean("testServiceBean", new TestServiceBean());
        SimpleAnkorServer server = new SimpleAnkorServer(application, "TestServer");
        server.start();

        server.receiveAction("model", RemoteMethodAction.create("testServiceBean.init(modelHolder)"));
        server.receiveAction("model.containers",
                             RemoteMethodAction.create("testServiceBean.addContainer(model.containers, 'tab1')"));

        server.receiveChange("model.containers['tab1'].filter.name", "A*");
        server.receiveChange("model.containers['tab1'].filter.type", "Bird");
        server.receiveAction("model.containers['tab1'].resultList",
                             RemoteMethodAction.create("testServiceBean.search(model.containers['tab1'])"));

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }

    public static class TestServiceBean {
        public void init(ModelHolder modelHolder) {
            LOG.info("TestServiceBean.init");
            modelHolder.setModel(new TestModel());
        }

        @SuppressWarnings("unchecked")
        public void addContainer(Map containers, String tabName) {
            LOG.info("TestServiceBean.addContainer");
            containers.put(tabName, new AnimalSearchContainer());
        }

        public void search(AnimalSearchContainer container) {
            AnimalFilter filter = container.getFilter();

            if (filter.getType() == AnimalType.Bird) {

                List<Animal> animals = new ArrayList<Animal>();
                animals.add(new Animal("Adler", AnimalType.Bird));
                animals.add(new Animal("Amsel", AnimalType.Bird));

                container.setResultList(animals);
            }
        }
    }




    @Test
    public void test_method_action_result() throws Exception {

        Application application = SimpleApplication.withModelType(TestModel.class)
                                                   .withBean("testServiceBean", new TestServiceBean2());
        SimpleAnkorServer server = new SimpleAnkorServer(application, "TestServer");
        server.start();

        server.receiveAction("", RemoteMethodAction.create("testServiceBean.init()").withResultIn(""));

        server.receiveAction("", RemoteMethodAction.create("testServiceBean.openAnimalSearch()")
                                                   .withResultIn("model.containers['tab1']"));

        server.receiveChange("model.containers['tab1'].filter.name", "A*");
        server.receiveChange("model.containers['tab1'].filter.type", "Bird");

        server.receiveAction("", RemoteMethodAction.create("testServiceBean.search(model.containers['tab1'].filter)")
                                                   .withResultIn("model.containers['tab1'].resultList"));

        Object model = application.getModelHolder().getModel();
        Assert.assertNotNull(model);
    }

    public static class TestServiceBean2 {
        public TestModel init() {
            return new TestModel();
        }

        @SuppressWarnings("unchecked")
        public AnimalSearchContainer openAnimalSearch() {
            return new AnimalSearchContainer();
        }

        public List<Animal> search(AnimalFilter filter) {

            if (filter.getType() == AnimalType.Bird) {

                List<Animal> animals = new ArrayList<Animal>();
                animals.add(new Animal("Adler", AnimalType.Bird));
                animals.add(new Animal("Amsel", AnimalType.Bird));

                return animals;
            }

            return null;
        }
    }

}
