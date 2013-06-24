package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.TabIds;
import at.irian.ankorman.sample1.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.animal.AnimalType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import static at.irian.ankor.fx.binding.BindingsBuilder.newBinding;
import static at.irian.ankorman.sample1.fxclient.App.application;
import static at.irian.ankorman.sample1.fxclient.App.facade;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailTabController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

    @FXML
    private Text message;
    @FXML
    private javafx.scene.control.Tab tab;
    @FXML
    private TextInputControl name;
    @FXML
    private ComboBox<AnimalType> type;
    @FXML
    private ComboBox<AnimalFamily> family;

    private String tabId = TabIds.next();

    private BindingContext bindingContext = new BindingContext();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab.setText(String.format("Animal Detail (%s)", tabId));
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                getTabRef().delete();
            }
        });
        facade().createAnimalDetailTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                Ref animalRef = getTabRef().sub("model").sub("animal");
                Ref selItemsRef = getTabRef().sub("model").sub("selectItems");

                newBinding()
                        .bindValue(animalRef.sub("name"))
                        .toInput(name)
                        .createWithin(bindingContext);
                newBinding()
                        .bindValue(animalRef.sub("type"))
                        .toCombo(type)
                        .withSelectItems(selItemsRef.sub("types"))
                        .createWithin(bindingContext);
                newBinding()
                        .bindValue(animalRef.sub("family"))
                        .toCombo(family)
                        .withSelectItems(selItemsRef.sub("families"))
                        .createWithin(bindingContext);

                name.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                        message.setText("");
                    }
                });
            }
        });
    }

    private Ref getTabRef() {
        Ref rootRef = application().getRefFactory().rootRef();
        return rootRef.sub(String.format("tabs.%s", tabId));
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().saveAnimal(getTabRef(), new ActionCompleteCallback() {
            public void onComplete() {
                message.setText("Saved!");
            }
        });
    }

}
