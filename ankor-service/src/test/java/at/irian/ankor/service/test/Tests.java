package at.irian.ankor.service.test;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.DefaultApplication;
import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.event.ChangeListener;
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

        DefaultApplication application = SimpleApplication.create(MyModel.class);
        SimpleAnkorServer server = SimpleAnkorServer.create(application, "server");

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

        final DefaultApplication serverApp = SimpleApplication.create(MyModel.class);
        SimpleAnkorServer server = SimpleAnkorServer.create(serverApp, "server");
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new ActionListener() {
            @Override
            public void processAction(Ref modelContext, Action action) {
                if (action.name().equals("init")) {
                    LOG.info("Creating new MyModel");
                    modelContext.root().setValue(new MyModel());
                    modelContext.fire(SimpleAction.create("initialized"));
                }
            }
        });
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new NewContainerActionListener());
        serverApp.getListenerRegistry().registerRemoteActionListener(null, new AnimalSearchActionListener());

        final DefaultApplication clientApp = SimpleApplication.create(MyModel.class);
        SimpleAnkorServer client = SimpleAnkorServer.create(clientApp, "client");
        clientApp.getListenerRegistry().registerRemoteActionListener(null, new ActionListener() {
            @Override
            public void processAction(Ref modelContext, Action action) {
                if (action.name().equals("initialized")) {
                    Ref containerRef = modelContext.root().sub("containers['tab1']");
                    containerRef.setValue(null);

                    clientApp.getListenerRegistry().registerRemoteChangeListener(containerRef, new ChangeListener() {

                        @Override
                        public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
                            LOG.info("new container {}", watchedProperty.getValue());

                            watchedProperty.sub("filter.name").setValue("A*");
                            watchedProperty.sub("filter.type").setValue(AnimalType.Bird);
                            watchedProperty.fire(SimpleAction.create("search"));
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
