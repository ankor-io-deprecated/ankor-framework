package at.irian.ankor.sample.fx;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.server.SimpleAnkorServer;
import at.irian.ankor.sample.fx.model.AnimalType;
import at.irian.ankor.sample.fx.view.AnimalSearchTab;
import at.irian.ankor.sample.fx.view.Tab;
import at.irian.ankor.sample.fx.view.Tabs;
import at.irian.ankor.sample.fx.view.ViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends javafx.application.Application {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Main.class);

    private static Application serverApp;
    static Application clientApp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        setupClientServer();

        serverApp.getListenerRegistry().registerRemoteActionListener(null, new ModelActionListener() {

            public void handleModelAction(ModelRef actionContext, ModelAction action) {
                if ("init".equals(action.name())) {
                    LOG.info("Creating new TestModel");

                    actionContext.root().setValue(new ViewModel());

                    actionContext.root().sub("userName").setValue("Toni Polster");

                    Tabs tabs = actionContext.root().sub("tabs").getValue();
                    Tab tab = tabs.newTab();
                    actionContext.sub(String.format("tabs.getTab('%s').model", tab.getId())).setValue(new AnimalSearchTab());

                    ModelRef animalSearchTabRef = actionContext.root().sub(String.format("tabs.getTab('%s').model", tab.getId()));
                    animalSearchTabRef.sub("filter.name").setValue("Eagle");
                    animalSearchTabRef.sub("filter.type").setValue(AnimalType.Bird);
                    //modelRef.fireAction("initialized");
                }
            }
        });

        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private void setupClientServer() {

        serverApp = new Application(ViewModel.class);
        SimpleAnkorServer server = new SimpleAnkorServer(serverApp, "animalServer");
        server.init();

        clientApp = new Application(ViewModel.class);
        SimpleAnkorServer client = new SimpleAnkorServer(clientApp, "animalClient");
        client.init();

        server.setRemoteServer(client);
        client.setRemoteServer(server);
    }

}
