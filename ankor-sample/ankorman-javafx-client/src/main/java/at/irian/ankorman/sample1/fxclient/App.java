package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.fx.app.SimpleLocalAppServiceBuilder;
import at.irian.ankor.fx.app.SocketAppServiceBuilder;
import at.irian.ankor.ref.RefFactory;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Object serviceBean = Class.forName("at.irian.ankorman.sample1.server.ServiceBean").newInstance();

        //createSimpleAppService(serviceBean);
        createSocketAppService(serviceBean);

        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private void createSimpleAppService(Object serviceBean) throws ClassNotFoundException {
        SimpleLocalAppServiceBuilder appServiceBuilder = new SimpleLocalAppServiceBuilder()
                .withModelType(Class.forName("at.irian.ankorman.sample1.model.ModelRoot"))
                .withBean("service", serviceBean);
        appService = appServiceBuilder.create();
    }

    private void createSocketAppService(Object serviceBean) throws ClassNotFoundException {
        SocketAppServiceBuilder appServiceBuilder = new SocketAppServiceBuilder()
                .withModelType(Class.forName("at.irian.ankorman.sample1.model.ModelRoot"))
                .withBean("service", serviceBean);
        appService = appServiceBuilder.create();
    }

    public static RefFactory refFactory() {
        return appService.getRefFactory();
    }
}