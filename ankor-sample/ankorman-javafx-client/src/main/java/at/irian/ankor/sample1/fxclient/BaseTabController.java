package at.irian.ankor.sample1.fxclient;

import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.sample1.fxclient.App.refFactory;

/**
 * @author Thomas Spiegl
 */
public abstract class BaseTabController implements Initializable {

    @FXML
    protected javafx.scene.control.Tab tab;

    private final String tabId;

    protected BindingContext bindingContext;

    protected BaseTabController(String tabId) {
        this.tabId = tabId;
        this.bindingContext = new BindingContext();
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
                bindingContext.unbind();
                getTabRef().setValue(null);
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
