package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionBuilder;
import at.irian.ankor.fx.binding.fxref.FxRef;
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
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedParameters")
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
    protected TableColumn<Map, String> nameColumn;
    @FXML
    protected TableColumn<Map, String> typeColumn;
    @FXML
    protected TableColumn<Map, String> familyColumn;
    @FXML
    protected TableColumn<Map, String> buttonColumn;

    @FXML
    protected Button save;

    public AnimalSearchTabController(String tabId) {
        super(tabId);
    }

    @Override
    public void initialize() {
        final FxRef tabRef = getTabRef();
        FXControllerAnnotationSupport.scan(tabRef, this);

        FxRef modelRef = tabRef.appendPath("model");
        Ref filterRef = tabRef.appendPath("model.filter");
        Ref selItemsRef = tabRef.appendPath("model.selectItems");

        tab.textProperty().bind(FxRefs.observableString(tabRef.appendPath("name")));

        name.textProperty().bindBidirectional(FxRefs.stringProperty(filterRef.appendPath("name")));

        type.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("types")));
        type.valueProperty().bindBidirectional(FxRefs.enumProperty(filterRef.appendPath("type")));

        family.itemsProperty().bind(FxRefs.<Enum>observableList(selItemsRef.appendPath("families")));
        family.valueProperty().bindBidirectional(FxRefs.enumProperty(filterRef.appendPath("family")));

        // bind table
        animalTable.itemsProperty().bind(modelRef.appendPath("animals").<Map>fxObservableList());
        nameColumn.setCellValueFactory(new MapValueFactory<String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        nameColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Map, String> t) {
                        int rowNum = t.getTablePosition().getRow();
                        Ref rowNameRef = getTabRef().appendPath(String.format("model.animals[%d].name", rowNum));
                        AnkorPatterns.changeValueLater(rowNameRef,
                                                       t.getNewValue()); // we must not directly access the model context from a non-dispatching thread
                    }
                });

        typeColumn.setCellValueFactory(new MapValueFactory<String>("type"));
        familyColumn.setCellValueFactory(new MapValueFactory<String>("family"));
        buttonColumn.setCellValueFactory(new MapValueFactory<String>("uuid"));
        buttonColumn.setCellFactory(new Callback<TableColumn<Map, String>, TableCell<Map, String>>() {
            @Override
            public TableCell<Map, String> call(TableColumn<Map, String> tableColumn) {
                return new TableCell<Map,String>() {
                    @Override
                    protected void updateItem(final String uuid, boolean empty) {
                        if (!empty) {
                            createCellButtons(this, uuid);
                        }
                    }

                };
            }
        });

        // set focus to "name" input control
        name.requestFocus();
    }

    private void createCellButtons(TableCell<Map, String> cell, final String uuid) {

        Button deleteButton = new Button("delete");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteAnimal(uuid);
            }
        });

        Button editButton = new Button("edit");
        editButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                editAnimal(uuid);
            }
        });

        HBox box = new HBox();
        box.getChildren().addAll(deleteButton, editButton);
        cell.setGraphic(box);
    }

    private void deleteAnimal(String uuid) {
        getTabRef().appendPath("model").fire(new ActionBuilder().withName("delete").withParam("uuid", uuid).create());
    }

    private void editAnimal(String uuid) {
        getTabRef().appendPath("model").fire(new ActionBuilder().withName("edit").withParam("uuid", uuid).create());
    }

    public void save(ActionEvent actionEvent) {
        getTabRef().appendPath("model").fire(new Action("save"));
    }

}
