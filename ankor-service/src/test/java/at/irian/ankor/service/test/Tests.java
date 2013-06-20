package at.irian.ankor.service.test;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.DefaultApplication;
import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.action.ActionListener;
import at.irian.ankor.change.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.service.SimpleAnkorServer;
import at.irian.ankor.service.test.animal.AnimalSearchActionListener;
import at.irian.ankor.service.test.animal.AnimalType;
import org.junit.Test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class Tests {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tests.class);

    @Test
    public void test_animal_search() throws Exception {

        DefaultApplication application = SimpleApplication.create(TestModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(application, "server");

        application.getListenerRegistry().registerRemoteActionListener(null, new InitActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        application.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        server.receiveAction(null, SimpleAction.create("init"));

        server.receiveChange("root.containers['tab1']", null);
        server.receiveAction("root.containers['tab1']", SimpleAction.create("newAnimalSearchContainer"));

        server.receiveChange("root.containers['tab1'].filter.name", "A*");
        server.receiveChange("root.containers['tab1'].filter.type", "Bird");
        server.receiveAction("root.containers['tab1']", SimpleAction.create("search"));

    }


    @Test
    public void test_animal_search_with_mock_client() throws Exception {

        final DefaultApplication serverApp = SimpleApplication.create(TestModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(serverApp, "server");
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new ActionListener() {
            @Override
            public void processAction(Ref actionContext, Action action) {
                if (action.name().equals("init")) {
                    LOG.info("Creating new TestModel");
                    actionContext.root().setValue(new TestModel());
                    actionContext.fire(SimpleAction.create("initialized"));
                }
            }
        });
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        final DefaultApplication clientApp = SimpleApplication.create(TestModel.class);
        SimpleAnkorServer client = new SimpleAnkorServer(clientApp, "client");
        clientApp.getListenerRegistry().registerRemoteActionListener(null, new ActionListener() {
            @Override
            public void processAction(Ref actionContext, Action action) {
                if (action.name().equals("initialized")) {
                    Ref containerRef = actionContext.root().sub("containers['tab1']");
                    containerRef.setValue(null);

                    clientApp.getListenerRegistry().registerRemoteChangeListener(containerRef, new ChangeListener() {

                        @Override
                        public void processChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
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

        server.receiveAction(null, SimpleAction.create("init"));

//        server.receiveChange("containers['tab1']", null);
//        server.receiveAction("containers['tab1']", "newAnimalSearchContainer");

//        server.receiveChange("containers['tab1'].filter.name", "A*");
//        server.receiveChange("containers['tab1'].filter.type", "Bird");
//        server.receiveAction("containers['tab1']", "search");

    }


}
