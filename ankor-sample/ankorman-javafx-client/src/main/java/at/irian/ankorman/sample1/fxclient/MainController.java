package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionBuilder;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankor.ref.listener.RefListeners.addTreeChangeListener;
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
        final Ref tabsRef = rootRef.append("tabs");

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

                Map<String,?> tabs = tabsRef.getValue();
                for (String tabId : tabs.keySet()) {
                    Ref tabRef = tabsRef.append(tabId);
                    showTab(tabRef);
                }

            }
        });

        addTreeChangeListener(tabsRef, new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                if (changedProperty.parent().equals(tabsRef)) {
                    String tabId = changedProperty.propertyName();
                    if (changedProperty.getValue() == null) {
                        for (Tab tab : tabPane.getTabs()) {
                            if (tab.getUserData().equals(tabId)) {
                                tabPane.getTabs().remove(tab);
                                break;
                            }
                        }
                    } else {
                        showTab(changedProperty);
                    }
                }
            }
        });

        rootRef.fire(new Action("init"));
    }

    private void showTab(Ref tabRef) {
        String tabId = tabRef.propertyName();
        Ref typeRef = tabRef.append("type");
        TabType tabType = TabType.valueOf((String) typeRef.getValue());
        new TabLoader(tabType, tabId).showTab(tabPane);
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().rootRef().append("tabs");
        tabsRef.fire(new ActionBuilder().withName(animalSearchTab.getActionName()).create());
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().rootRef().append("tabs");
        tabsRef.fire(new ActionBuilder().withName(animalDetailTab.getActionName()).create());
    }
}
