package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankor.fx.controller.FXControllerAnnotationSupport.annotationSupport;

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
        annotationSupport().registerChangeListener(this, getTabRef());
    }

    public void initialize() {
        Ref modelRef = getTabRef().append("model");
        Ref animalRef = modelRef.append("animal");
        Ref selItemsRef = modelRef.append("selectItems");

        bindValue(getTabRef().append("name"))
                .toTabText(tab)
                .createWithin(bindingContext);
        bindValue(animalRef.append("name"))
                .toInput(name)
                .withEditable(modelRef.append("editable"))
                .withFloodControlDelay(500L)
                .createWithin(bindingContext);
        bindValue(modelRef.append("nameStatus"))
                .toText(nameStatus)
                .createWithin(bindingContext);
        bindValue(animalRef.append("type"))
                .toInput(type)
                .withSelectItems(selItemsRef.append("types"))
                .withEditable(modelRef.append("editable"))
                .createWithin(bindingContext);
        bindValue(animalRef.append("family"))
                .toInput(family)
                .withSelectItems(selItemsRef.append("families"))
                .withEditable(modelRef.append("editable"))
                .createWithin(bindingContext);

        name.requestFocus();
    }

    @ChangeListener(pattern = "model.animal.name")
    public void onNameChanged() {
        System.out.println("Animal name changed changed to " + getTabRef().append("model.animal.name").getValue());
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        getTabRef().append("model").fire(new Action("save"));
    }

}
