package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailTabController extends BaseTabController {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

    @FXML
    protected TextInputControl name;
    @FXML
    protected Text nameStatus;
    @FXML
    protected ComboBox<Enum> type;
    @FXML
    protected ComboBox<Enum> family;

    public AnimalDetailTabController(String tabId) {
        super(tabId);
    }

    public void initialize() {
        Ref tabRef = getTabRef();
        FXControllerAnnotationSupport.scan(tabRef, this);

        Ref modelRef = tabRef.appendPath("model");
        Ref animalRef = modelRef.appendPath("animal");
        Ref selItemsRef = modelRef.appendPath("selectItems");

        bindValue(tabRef.appendPath("name"))
                .toProperty(tab.textProperty())
                .createWithin(bindingContext);
        bindValue(animalRef.appendPath("name"))
                .toInput(name)
                .createWithin(bindingContext);
        bindValue(modelRef.appendPath("editable"))
                .toProperty(name.editableProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.appendPath("nameStatus"))
                .toText(nameStatus)
                .createWithin(bindingContext);
        bindValue(animalRef.appendPath("type"))
                .toInput(type)
                .withSelectItems(selItemsRef.appendPath("types"))
                .createWithin(bindingContext);
        bindValue(animalRef.appendPath("family"))
                .toInput(family)
                .withSelectItems(selItemsRef.appendPath("families"))
                .createWithin(bindingContext);

        name.requestFocus();
    }

    @ChangeListener(pattern = ".model.animal.name")
    public void onNameChanged() {
        System.out.println("Animal name changed changed to " + getTabRef().appendPath("model.animal.name").getValue());
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        getTabRef().appendPath("model").fire(new Action("save"));
    }

}
