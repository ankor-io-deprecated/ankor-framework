package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionBuilder;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.FxRefs;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
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

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = refFactory().ref("root");
        FXControllerAnnotationSupport.scan(rootRef, this);
        rootRef.fire(new Action("init"));
    }

    @ChangeListener(pattern = "root.contentPane.panels[(*)]")
    public void tabsRefChanged(Ref tabRef) {
        String tabId = tabRef.propertyName();
        if (tabRef.getValue() == null) {
            for (Tab tab : tabPane.getTabs()) {
                if (tab.getUserData().equals(tabId)) {
                    tabPane.getTabs().remove(tab);
                    break;
                }
            }
        } else {
            showTab(tabRef);
        }
    }

    @ChangeListener(pattern = "root")
    public void rootRefChanged() {
        Ref rootRef = refFactory().ref("root");
        Ref tabsRef = rootRef.appendPath("contentPane.panels");

        userName.textProperty().bind(FxRefs.observableString(rootRef.appendPath("userName")));

        serverStatus.textProperty().bind(FxRefs.observableString(rootRef.appendPath("serverStatus")));

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
        Ref contentPaneRef = refFactory().ref("root.contentPane");
        contentPaneRef.fire(new ActionBuilder().withName(TabType.animalSearch.getActionName()).create());
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        Ref contentPaneRef = refFactory().ref("root.contentPane");
        contentPaneRef.fire(new ActionBuilder().withName(TabType.animalDetail.getActionName()).create());
    }
}
