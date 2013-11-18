package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class AnimalSearchTabController extends BaseTabController {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

    @FXML
    protected TextInputControl name;
    @FXML
    protected ComboBox<Enum> type;
    @FXML

    protected ComboBox<Enum> family;

    @FXML
    protected TableView<Map> animalTable;
    @FXML
    protected TableColumn<Map, String> animalName;
    @FXML
    protected TableColumn<Map, String> animalType;
    @FXML
    protected TableColumn<Map, String> animalFamily;

    @FXML
    protected Button save;

    public AnimalSearchTabController(String tabId) {
        super(tabId);
    }

    @Override
    public void initialize() {
        final Ref tabRef = getTabRef();
        FXControllerAnnotationSupport.scan(tabRef, this);

        Ref modelRef = tabRef.appendPath("model");
        Ref filterRef = tabRef.appendPath("model.filter");
        Ref selItemsRef = tabRef.appendPath("model.selectItems");

        tab.textProperty().bind(FxRefs.observableString(tabRef.appendPath("name")));

        name.textProperty().bindBidirectional(FxRefs.stringProperty(filterRef.appendPath("name")));

        type.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("types")));
        type.valueProperty().bindBidirectional(FxRefs.enumProperty(filterRef.appendPath("type")));

        family.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("families")));
        family.valueProperty().bindBidirectional(FxRefs.enumProperty(filterRef.appendPath("family")));

        // bind table
        animalTable.itemsProperty().bind(FxRefs.<Map>observableList(modelRef.appendPath("animals")));
        animalName.setCellValueFactory(new MapValueFactory<String>("name"));
        animalName.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        animalName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Map, String> t) {
                        int rowNum = t.getTablePosition().getRow();
                        Ref rowNameRef = getTabRef().appendPath(String.format("model.animals[%d].name", rowNum));
                        AnkorPatterns.changeValueLater(rowNameRef,
                                                       t.getNewValue()); // we must not directly access the model context from a non-dispatching thread
                    }
                });

        animalType.setCellValueFactory(new MapValueFactory<String>("type"));
        animalFamily.setCellValueFactory(new MapValueFactory<String>("family"));

        // set focus to "name" input control
        name.requestFocus();
    }

    public void save(@SuppressWarnings("UnusedParameters") ActionEvent actionEvent) {
        getTabRef().appendPath("model").fire(new Action("save"));
    }
}
