package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.pattern.AnkorPatterns;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Thomas Spiegl
 */
public abstract class BaseTabController implements Initializable {

    @FXML
    protected javafx.scene.control.Tab tab;

    private final String tabId;

    protected BaseTabController(String tabId) {
        this.tabId = tabId;
    }

    public FxRef getTabRef() {
        return FxRefs.refFactory().ref(String.format("root.contentPane.panels.%s", tabId));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tab.setUserData(tabId);

        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnkorPatterns.deleteItemLater(FxRefs.refFactory().ref("root.contentPane.panels"), tabId);
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        });
    }

    protected abstract void initialize();

}
