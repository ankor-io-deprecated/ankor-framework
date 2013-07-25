package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.refFactory;
import static at.irian.ankorman.sample1.fxclient.TabType.animalDetailTab;
import static at.irian.ankorman.sample1.fxclient.TabType.animalSearchTab;

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
        rootRef.addPropChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref watchedProperty, Ref changedProperty) {
                Ref rootRef = refFactory().rootRef();

                userName.setText((String) rootRef.append("userName").getValue());

                bindValue(rootRef.append("serverStatus"))
                        .toText(serverStatus)
                        .createWithin(bindingContext);
            }
        });
        rootRef.fireAction(new SimpleAction("init"));

    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        new TabLoader(animalSearchTab).loadTabTo(tabPane);
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        new TabLoader(animalDetailTab).loadTabTo(tabPane);
    }
}
