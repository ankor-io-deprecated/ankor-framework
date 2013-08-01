package at.irian.ankorman.sample2.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;
import static at.irian.ankorman.sample2.fxclient.TabType.animalDetailTab;
import static at.irian.ankorman.sample2.fxclient.TabType.animalSearchTab;

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
        Ref rootRef = refFactory().rootRef();

        RefListeners.addPropChangeListener(rootRef, new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                Ref rootRef = refFactory().rootRef();

                bindValue(rootRef.append("userName"))
                        .toText(userName)
                        .createWithin(bindingContext);

                bindValue(rootRef.append("serverStatus"))
                        .toText(serverStatus)
                        .createWithin(bindingContext);
            }
        });

        rootRef.fireAction(new Action("init"));
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        new TabLoader(animalSearchTab).loadTabTo(tabPane);
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        new TabLoader(animalDetailTab).loadTabTo(tabPane);
    }
}
