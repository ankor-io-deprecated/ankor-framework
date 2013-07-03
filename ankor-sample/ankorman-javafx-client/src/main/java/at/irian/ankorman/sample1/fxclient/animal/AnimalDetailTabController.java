package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.animal.AnimalType;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.facade;
import static at.irian.ankorman.sample1.fxclient.App.refFactory;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailTabController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

    @FXML
    protected javafx.scene.control.Tab tab;
    @FXML
    protected TextInputControl name;
    @FXML
    protected Text nameStatus;
    @FXML
    protected ComboBox<AnimalType> type;
    @FXML
    protected ComboBox<AnimalFamily> family;

    private final String tabId;

    private BindingContext bindingContext = new BindingContext();

    public AnimalDetailTabController(String tabId) {
        this.tabId = tabId;
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                getTabRef().setValue(null);
            }
        });
        Ref modelRef = getTabRef().append("model");
        Ref animalRef = modelRef.append("animal");
        Ref selItemsRef = modelRef.append("selectItems");

        bindValue(getTabRef().append("name"))
                .toTabText(tab)
                .createWithin(bindingContext);
        bindValue(animalRef.append("name"))
                .toInput(name)
                .createWithin(bindingContext);
        bindValue(modelRef.append("nameStatus"))
                .toText(nameStatus)
                .createWithin(bindingContext);
        bindValue(animalRef.append("type"))
                .toInput(type)
                .withSelectItems(selItemsRef.append("types"))
                .createWithin(bindingContext);
        bindValue(animalRef.append("family"))
                .toInput(family)
                .withSelectItems(selItemsRef.append("families"))
                .createWithin(bindingContext);
    }

    private Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabs.%s", tabId));
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().saveAnimal(getTabRef(), new ActionCompleteCallback() {
            public void onComplete() {
            }
        });
    }

}
