package at.irian.ankorsamples.animals.fxclient.animal;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.fxclient.BaseTabController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.Map;

import static at.irian.ankor.fx.binding.ButtonBindingBuilder.onButtonClick;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;

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
    protected Button previous;
    @FXML
    protected Button next;

    @FXML
    protected Button save;

    public AnimalSearchTabController(String tabId) {
        super(tabId);
    }

    @Override
    public void initialize() {
        final Ref tabRef = getTabRef();
        FXControllerAnnotationSupport.scan(tabRef, this);

        Ref filterRef = tabRef.appendPath("model.filter");
        Ref selItemsRef = tabRef.appendPath("model.selectItems");
        Ref rowsRef = tabRef.appendPath("model.animals.rows");
        Ref paginatorRef = tabRef.appendPath("model.animals.paginator");

        bindTableColumns();

        bindValue(tabRef.appendPath("name"))
                .toTabText(tab)
                .createWithin(bindingContext);

        bindValue(filterRef.appendPath("name"))
                .toInput(name)
                .withFloodControlDelay(500L)
                .createWithin(bindingContext);

        bindValue(filterRef.appendPath("type"))
                .toInput(type)
                .withSelectItems(selItemsRef.appendPath("types"))
                .createWithin(bindingContext);

        bindValue(filterRef.appendPath("family"))
                .toInput(family)
                .withSelectItems(selItemsRef.appendPath("families"))
                .createWithin(bindingContext);

        bindValue(rowsRef)
                .toTable(animalTable)
                .createWithin(bindingContext);

        onButtonClick(previous)
                .callAction(new ClickAction<Ref>() {
                    @Override
                    public void onClick(Ref paginator) {
                        paginator.fire(new Action("previous"));
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(next)
                .callAction(new ClickAction<Ref>() {
                    @Override
                    public void onClick(Ref paginator) {
                        paginator.fire(new Action("next"));
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(save)
                .callAction(new ClickAction() {
                    @Override
                    public void onClick(Object value) {
                        tabRef.appendPath("model").fire(new Action("save"));
                    }
                }).create();

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
                        Ref rowNameRef = getTabRef().appendPath(String.format("model.animals.rows[%d].name", rowNum));
                        AnkorPatterns.changeValueLater(rowNameRef, t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new MapValueFactory<String>("type"));
        animalFamily.setCellValueFactory(new MapValueFactory<String>("family"));
    }

}
