package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.ModelRoot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.ankorContext;
import static at.irian.ankorman.sample1.fxclient.App.facade;

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

    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade().initApplication(new ActionCompleteCallback() {

            public void onComplete() {
                Ref rootRef = ankorContext().getRefFactory().rootRef();
                ModelRoot modelRoot = rootRef.getValue();

                userName.setText(modelRoot.getUserName());

                bindValue(rootRef.append("serverStatus"))
                        .toText(serverStatus)
                        .createWithin(bindingContext);

            }
        });
    }

    public void openAnimalSearchTab(ActionEvent actionEvent) {
        Tab tab;
        try {
            tab = FXMLLoader.load(getClass().getClassLoader().getResource("animal_search_tab.fxml"));
        } catch (IOException e) {
            throw new IllegalStateException("cannot load animal_search_tab.fxml", e);
        }
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void openAnimalDetailTab(ActionEvent actionEvent) {
        Tab tab;
        try {
            tab = FXMLLoader.load(getClass().getClassLoader().getResource("animal_detail_tab.fxml"));
        } catch (IOException e) {
            throw new IllegalStateException("cannot load animal_detail_tab.fxml", e);
        }
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
