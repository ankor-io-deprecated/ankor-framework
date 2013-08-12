package at.irian.ankor.sample1.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionBuilder;
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
import static at.irian.ankor.ref.listener.RefListeners.addTreeChangeListener;
import static at.irian.ankor.sample1.fxclient.App.refFactory;
import static at.irian.ankor.sample1.fxclient.TabType.animalDetailTab;
import static at.irian.ankor.sample1.fxclient.TabType.animalSearchTab;

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

        final Ref tabsRef = rootRef.append("tabs");
        addTreeChangeListener(tabsRef, new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                if (changedProperty.parent().equals(tabsRef)) {
                    Ref typeRef = changedProperty.append("type");
                    Ref tabIdRef = changedProperty.append("id");
                    TabType tabType = TabType.valueOf((String) typeRef.getValue());
                    String tabId = tabIdRef.getValue();
                    new TabLoader(tabType, tabId).showTab(tabPane);
                }
            }
        });

        rootRef.fireAction(new Action("init"));
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().rootRef().append("tabs");
        tabsRef.fireAction(new ActionBuilder().withName(animalSearchTab.getActionName()).create());
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().rootRef().append("tabs");
        tabsRef.fireAction(new ActionBuilder().withName(animalDetailTab.getActionName()).create());
    }
}
