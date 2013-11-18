package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.fxref.FxRef;
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

    @ChangeListener(pattern = "root")
    public void modelRootChanged() {
        FxRef rootRef = refFactory().ref("root");

        // bind user name
        userName.textProperty().bind(rootRef.appendPath("userName").<String>fxObservable());

        // bind server status display
        serverStatus.textProperty().bind(rootRef.appendPath("serverStatus").<String>fxObservable());

        // open initial tabs
        FxRef panelsRef = rootRef.appendPath("contentPane.panels");
        Map<String,?> tabs = panelsRef.getValue();
        for (String tabId : tabs.keySet()) {
            Ref tabRef = panelsRef.appendPath(tabId);
            showTab(tabRef);
        }
    }

    @ChangeListener(pattern = "root.contentPane.panels[(*)]")
    public void tabsChanged(Ref tabRef) {
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

    private void showTab(Ref tabRef) {
        String tabId = tabRef.propertyName();
        Ref typeRef = tabRef.appendPath("type");
        TabType tabType = TabType.valueOf((String) typeRef.getValue());
        new TabLoader(tabType, tabId).loadTabTo(tabPane);
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        refFactory().ref("root.contentPane").fire(new Action(TabType.animalSearch.getActionName()));
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        refFactory().ref("root.contentPane").fire(new Action(TabType.animalDetail.getActionName()));
    }
}
