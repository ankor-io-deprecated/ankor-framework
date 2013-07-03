package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.animal.AnimalDetailTabController;
import at.irian.ankorman.sample1.fxclient.animal.AnimalSearchTabController;
import at.irian.ankorman.sample1.model.ModelRoot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.facade;
import static at.irian.ankorman.sample1.fxclient.App.refFactory;

/**
 * @author Thomas Spiegl
 */
public class MainController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainController.class);

    @FXML
    private Text serverStatus;
    @FXML
    private TabPane tabPane;
    @FXML
    private Text userName;

    private BindingContext bindingContext = new BindingContext();

    private TabLoader tabLoader;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade().initApplication(new ActionCompleteCallback() {

            public void onComplete() {
                Ref rootRef = refFactory().rootRef();
                ModelRoot modelRoot = rootRef.getValue();

                userName.setText(modelRoot.getUserName());

                bindValue(rootRef.append("serverStatus"))
                        .toText(serverStatus)
                        .createWithin(bindingContext);

                tabLoader = new TabLoader(tabPane);
            }
        });
    }

    public void openAnimalSearchTab(ActionEvent actionEvent) {
        tabLoader.loadTab(AnimalSearchTabController.class, "animal_search_tab.fxml");
    }

    public void openAnimalDetailTab(ActionEvent actionEvent) {
        tabLoader.loadTab(AnimalDetailTabController.class, "animal_detail_tab.fxml");
    }
}
