package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.fx.app.SimpleLocalAppServiceBuilder;
import at.irian.ankor.fx.app.SocketAppServiceBuilder;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
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
                .withModelType(Class.forName("at.irian.ankorman.sample1.viewmodel.ModelRoot"))
                .withBean("service", serviceBean);
        appService = appServiceBuilder.create();
    }

    private void createSocketAppService(Object serviceBean) throws ClassNotFoundException {

        SocketAppServiceBuilder appServiceBuilder = new SocketAppServiceBuilder()
                .withModelRootFactory(new ModelRootFactory() {
                    @Override
                    public Object createModelRoot(Ref rootRef) {
                        try {
                            Class<?> modelRootType = Class.forName("at.irian.ankorman.sample1.viewmodel.ModelRoot");
                            Class<?> repoType = Class.forName("at.irian.ankorman.sample1.server.AnimalRepository");
                            Object repo = repoType.newInstance();
                            return modelRootType.getConstructor(Ref.class, repoType).newInstance(rootRef, repo);
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to create model root", e);
                        }
                    }
                })
                .withBean("service", serviceBean);
        appService = appServiceBuilder.create();
    }

    public static RefFactory refFactory() {
        return appService.getRefFactory();
    }
}