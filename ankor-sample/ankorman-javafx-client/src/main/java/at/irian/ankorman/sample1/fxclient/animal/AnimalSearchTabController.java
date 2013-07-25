package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.BaseTabController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

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
    protected TableView<Object> animalTable;
    @FXML
    protected TableColumn<Object, String> animalName;
    @FXML
    protected TableColumn<Object, String> animalType;
    @FXML
    protected TableColumn<Object, String> animalFamily;

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
        Ref filterRef = tabRef.append("model.filter");
        Ref selItemsRef = tabRef.append("model.selectItems");
        Ref rowsRef = tabRef.append("model.animals.rows");
        Ref paginatorRef = tabRef.append("model.animals.paginator");

        bindTableColumns();

        bindValue(tabRef.append("name"))
                .toTabText(tab)
                .createWithin(bindingContext);

        bindValue(filterRef.append("name"))
                .toInput(name)
                .createWithin(bindingContext);

        bindValue(filterRef.append("type"))
                .toInput(type)
                .withSelectItems(selItemsRef.append("types"))
                .createWithin(bindingContext);

        bindValue(filterRef.append("family"))
                .toInput(family)
                .withSelectItems(selItemsRef.append("families"))
                .createWithin(bindingContext);

        bindValue(rowsRef)
                .toTable(animalTable)
                .createWithin(bindingContext);

        onButtonClick(previous)
                .callAction(new ClickAction<Ref>() {
                    @Override
                    public void onClick(Ref paginator) {
                        paginator.fireAction(new SimpleAction("previous"));
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(next)
                .callAction(new ClickAction<Ref>() {
                    @Override
                    public void onClick(Ref paginator) {
                        paginator.fireAction(new SimpleAction("next"));
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(save)
                .callAction(new ClickAction() {
                    @Override
                    public void onClick(Object value) {
                        tabRef.append("model").fireAction(new SimpleAction("save"));
                    }
                }).create();

        name.requestFocus();
    }

    @SuppressWarnings("unchecked")
    private void bindTableColumns() {

        animalName.setCellValueFactory(new PropertyValueFactory<Object, String>("name"));
        animalName.setCellFactory(TextFieldTableCell.<Object>forTableColumn());
        animalName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Object, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Object, String> t) {
                        int rowNum = t.getTablePosition().getRow();
                        getTabRef().append(String.format("model.animals.rows[%d].name", rowNum)).setValue(t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new PropertyValueFactory<Object, String>("type"));
        animalFamily.setCellValueFactory(new PropertyValueFactory<Object, String>("family"));
    }

}
