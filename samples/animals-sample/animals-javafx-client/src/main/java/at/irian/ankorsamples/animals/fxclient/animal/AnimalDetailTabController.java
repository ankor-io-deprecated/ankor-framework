package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.convert.ReverseBooleanConverter;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
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
        FXControllerAnnotationSupport.scan(tabRef, this);

        FxRef modelRef = tabRef.appendPath("model");
        FxRef animalRef = modelRef.appendPath("animal");
        FxRef selItemsRef = modelRef.appendPath("selectItems");

        ObservableValue<Boolean> disableProperty = FxRefs.observable(modelRef.appendPath("editable"),
                                                                     ReverseBooleanConverter.instance());

        tab.textProperty().bind(tabRef.appendPath("name").<String>fxObservable());

        name.textProperty().bindBidirectional(animalRef.appendPath("name").<String>fxProperty());
        name.disableProperty().bind(disableProperty);

        nameStatus.textProperty().bind(modelRef.appendPath("nameStatus").<String>fxObservable());

        type.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("types")));
        type.valueProperty().bindBidirectional(FxRefs.enumProperty(animalRef.appendPath("type")));
        type.disableProperty().bind(disableProperty);

        family.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("families")));
        family.valueProperty().bindBidirectional(FxRefs.enumProperty(animalRef.appendPath("family")));
        family.disableProperty().bind(disableProperty);

        name.requestFocus();
    }

    @ChangeListener(pattern = ".model.animal.name")
    public void onNameChanged() {
        LOG.debug("Animal name changed changed to " + getTabRef().appendPath("model.animal.name").getValue());
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        getTabRef().appendPath("model").fire(new Action("save"));
    }

}
