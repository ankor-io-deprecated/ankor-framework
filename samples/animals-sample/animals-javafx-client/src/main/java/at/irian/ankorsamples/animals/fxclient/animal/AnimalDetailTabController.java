package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.property.ViewModelListProperty;
import at.irian.ankor.fx.binding.property.ViewModelProperty;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

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

        tab.textProperty().bind(new ViewModelProperty<String>(tabRef, "name"));

        name.textProperty().bindBidirectional(new ViewModelProperty<String>(animalRef, "name"));

        name.editableProperty().bind(new ViewModelProperty<Boolean>(modelRef, "editable"));

        nameStatus.textProperty().bind(new ViewModelProperty<String>(modelRef, "nameStatus"));

        type.itemsProperty().bind(new ViewModelListProperty<Enum>(selItemsRef, "types"));
        type.valueProperty().bindBidirectional(new ViewModelProperty<Enum>(animalRef, "type"));

        family.itemsProperty().bind(new ViewModelListProperty<Enum>(selItemsRef, "families"));
        family.valueProperty().bindBidirectional(new ViewModelProperty<Enum>(animalRef, "family"));

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
