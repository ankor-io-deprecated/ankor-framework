package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.convert.ReverseBooleanConverter;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.controller.FXControllerSupport;
import at.irian.ankorsamples.animals.fxclient.BaseTabController;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailTabController extends BaseTabController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

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
        FxRef tabRef = getTabRef();
        FXControllerSupport.init(this, tabRef);

        FxRef modelRef = tabRef.appendPath("model");
        FxRef animalRef = modelRef.appendPath("animal");
        FxRef selItemsRef = modelRef.appendPath("selectItems");

        ObservableValue<Boolean> disableProperty = modelRef.appendPath("editable").fxProperty(ReverseBooleanConverter.instance());

        tab.textProperty().bind(tabRef.appendPath("name").<String>fxObservable());

        name.textProperty().bindBidirectional(animalRef.appendPath("name").<String>fxProperty());
        name.disableProperty().bind(disableProperty);

        nameStatus.textProperty().bind(modelRef.appendPath("nameStatus").<String>fxObservable());

        type.itemsProperty().bind(selItemsRef.appendPath("types").<Enum>fxObservableList());
        type.valueProperty().bindBidirectional(animalRef.appendPath("type").<Enum>fxProperty());
        type.disableProperty().bind(disableProperty);

        family.itemsProperty().bind(selItemsRef.appendPath("families").<Enum>fxObservableList());
        family.valueProperty().bindBidirectional(animalRef.appendPath("family").<Enum>fxProperty());
        family.disableProperty().bind(disableProperty);

        name.requestFocus();
    }

    @ChangeListener(pattern = ".model.animal.name")
    public void onNameChanged() {
        LOG.debug("Animal name changed changed to " + getTabRef().appendPath("model.animal.name").getValue());
    }

    public void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        getTabRef().appendPath("model").fire(new Action("save"));
    }

}
