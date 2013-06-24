package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.application.Application;
import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.fx.app.SimpleLocalAppServiceCreator;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.server.ServiceBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        ServiceBean serviceBean = new ServiceBean();

        SimpleLocalAppServiceCreator appServiceCreator = new SimpleLocalAppServiceCreator()
                .withModelType(ModelRoot.class)
                .withBean("service", serviceBean)
                .withServerStatusMessage(true);
        appService = appServiceCreator.create();
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