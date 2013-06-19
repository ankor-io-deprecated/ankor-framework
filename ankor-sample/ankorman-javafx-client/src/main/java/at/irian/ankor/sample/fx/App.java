package at.irian.ankor.sample.fx;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.fx.app.SimpleLocalApplicationService;
import at.irian.ankor.sample.fx.server.ServiceBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Thomas Spiegl
 */
public class App extends javafx.application.Application {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static AppService appService;
    private static ServiceFacade serviceFacade;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        appService = new SimpleLocalApplicationService()
                .withBean("service", new ServiceBean())
                .withServerStatusMessage(true)
                .create();
        serviceFacade = new ServiceFacade(appService);

        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    public static ServiceFacade facade() {
        return serviceFacade;
    }

    public static Application application() {
        return appService.getApplication();
    }

}