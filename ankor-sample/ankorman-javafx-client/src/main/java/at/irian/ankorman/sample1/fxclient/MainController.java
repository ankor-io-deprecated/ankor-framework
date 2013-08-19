package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionBuilder;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
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
import static at.irian.ankor.fx.controller.FXControllerAnnotationSupport.annotationSupport;
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

    public MainController() {
        annotationSupport().registerChangeListeners(this);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        refFactory().rootRef().fire(new Action("init"));
    }

    @ChangeListener(pattern = "root.tabs.*")
    public void tabsRefChanged(Ref changedProperty) {
        final Ref tabsRef = refFactory().rootRef().append("tabs");
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

    @ChangeListener(pattern = "root")
    public void rootRefChanged() {
        Ref rootRef = refFactory().rootRef();
        Ref tabsRef = rootRef.append("tabs");

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
