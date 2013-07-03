package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.ref.Ref;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankorman.sample1.fxclient.App.refFactory;

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

    public Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabs.%s", tabId));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                getTabRef().setValue(null);
            }
        });
        initialize();
    }

    protected abstract void initialize();

}
