package at.irian.ankorsamples.animals.fxclient;

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
import static at.irian.ankorsamples.animals.fxclient.App.refFactory;

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
        refFactory().ref("root").fire(new Action("init"));
    }

    @ChangeListener(pattern = "root.tabs.*")
    public void tabsRefChanged(Ref changedProperty) {
        final Ref tabsRef = refFactory().ref("root.tabs");
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
        Ref rootRef = refFactory().ref("root");
        Ref tabsRef = rootRef.appendPath("tabs");

        bindValue(rootRef.appendPath("userName"))
                .toText(userName)
                .createWithin(bindingContext);

        bindValue(rootRef.appendPath("serverStatus"))
                .toText(serverStatus)
                .createWithin(bindingContext);

        Map<String,?> tabs = tabsRef.getValue();
        for (String tabId : tabs.keySet()) {
            Ref tabRef = tabsRef.appendPath(tabId);
            showTab(tabRef);
        }
    }

    private void showTab(Ref tabRef) {
        String tabId = tabRef.propertyName();
        Ref typeRef = tabRef.appendPath("type");
        TabType tabType = TabType.valueOf((String) typeRef.getValue());
        new TabLoader(tabType, tabId).showTab(tabPane);
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().ref("root.tabs");
        tabsRef.fire(new ActionBuilder().withName(TabType.animalSearchTab.getActionName()).create());
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref tabsRef = refFactory().ref("root").appendPath("tabs");
        tabsRef.fire(new ActionBuilder().withName(TabType.animalDetailTab.getActionName()).create());
    }
}
