package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerSupport;
import at.irian.ankor.ref.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

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
    @FXML
    private HBox localesBox;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = FxRefs.refFactory().ref("root");
        FXControllerSupport.init(this, rootRef);
        rootRef.fire(new Action("init"));
    }

    @ChangeListener(pattern = "root")
    public void modelRootChanged() {
        final FxRef rootRef = FxRefs.refFactory().ref("root");

//        label_Animal.setProperty(rootRef.appendPath("labels").appendLiteralKey("Animal").fxProperty());

        // bind user name
        userName.textProperty().bind(rootRef.appendPath("userName").<String>fxObservable());

        // bind server status display
        serverStatus.textProperty().bind(rootRef.appendPath("serverStatus").<String>fxObservable());

        // bind locale
        final FxRef localeRef = rootRef.appendPath("locale");
        List<String> supportedLocales = rootRef.appendPath("supportedLocales").getValue();
        final ToggleGroup localesGroup = new ToggleGroup();
        for (String locale : supportedLocales) {
            ToggleButton tb = new ToggleButton(locale);
            tb.setToggleGroup(localesGroup);
            tb.setUserData(locale);
            localesBox.getChildren().add(tb);
        }
        FxRefs.bindToggleGroup(localesGroup, localeRef);

        // open initial tabs
        FxRef panelsRef = rootRef.appendPath("contentPane.panels");
        Map<String,?> tabs = panelsRef.getValue();
        for (String tabId : tabs.keySet()) {
            Ref tabRef = panelsRef.appendPath(tabId);
            showTab(tabRef);
        }
    }

    @ChangeListener(pattern = {"root.contentPane.(panels)",
                               "root.contentPane.(panels).*"})
    public void tabsChanged(Ref panelsRef) {
        Map<String, ?> panels = panelsRef.getValue();

        Set<String> currentOpenTabIds = new HashSet<>(tabPane.getTabs().size());
        for (Tab tab : tabPane.getTabs()) {
            currentOpenTabIds.add((String) tab.getUserData());
        }

        // are there any new panels to show as tab?
        for (String panelId : panels.keySet()) {
            if (!currentOpenTabIds.contains(panelId)) {
                showTab(panelsRef.appendLiteralKey(panelId));
            }
        }

        // are there any open tabs to close?
        Iterator<Tab> tabsIterator = tabPane.getTabs().iterator();
        while (tabsIterator.hasNext()) {
            Tab tab = tabsIterator.next();
            String tabId = (String) tab.getUserData();
            if (!panels.keySet().contains(tabId)) {
                tabsIterator.remove();
            }
        }
    }

    private void showTab(Ref tabRef) {
        String tabId = tabRef.propertyName();
        Ref typeRef = tabRef.appendPath("type");
        TabType tabType = TabType.valueOf((String) typeRef.getValue());
        new TabLoader(tabType, tabId).loadTabTo(tabPane);
    }

    public void openAnimalSearchTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        FxRefs.refFactory().ref("root.contentPane").fire(new Action(TabType.animalSearch.getActionName()));
    }

    public void openAnimalDetailTab(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        FxRefs.refFactory().ref("root.contentPane").fire(new Action(TabType.animalDetail.getActionName()));
    }
}
