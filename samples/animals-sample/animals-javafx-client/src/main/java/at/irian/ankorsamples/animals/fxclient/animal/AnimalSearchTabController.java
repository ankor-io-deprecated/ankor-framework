package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.property.ViewModelListProperty;
import at.irian.ankor.fx.binding.property.ViewModelProperty;
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

        bindTableColumns();

        tab.textProperty().bind(new ViewModelProperty<String>(tabRef, "name"));

        // TODO flood control
        name.textProperty().bindBidirectional(new ViewModelProperty<String>(filterRef, "name"));

        type.itemsProperty().bind(new ViewModelListProperty<Enum>(selItemsRef, "types"));
        type.valueProperty().bindBidirectional(new ViewModelProperty<Enum>(filterRef, "type"));

        family.itemsProperty().bind(new ViewModelListProperty<Enum>(selItemsRef, "families"));
        family.valueProperty().bindBidirectional(new ViewModelProperty<Enum>(filterRef, "family"));

        animalTable.itemsProperty().bind(new ViewModelListProperty<Map>(modelRef, "animals"));

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tabRef.appendPath("model").fire(new Action("save"));
            }
        });

        name.requestFocus();
    }

    @SuppressWarnings("unchecked")
    private void bindTableColumns() {

        animalName.setCellValueFactory(new MapValueFactory<String>("name"));
        animalName.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        animalName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Map, String> t) {
                        int rowNum = t.getTablePosition().getRow();
                        Ref rowNameRef = getTabRef().appendPath(String.format("model.animals[%d].name", rowNum));
                        AnkorPatterns.changeValueLater(rowNameRef, t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new MapValueFactory<String>("type"));
        animalFamily.setCellValueFactory(new MapValueFactory<String>("family"));
    }

}
