package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.BaseTabController;
import at.irian.ankorman.sample1.model.animal.Animal;
import at.irian.ankorman.sample1.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.animal.AnimalType;
import at.irian.ankorman.sample1.model.animal.Paginator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import static at.irian.ankor.fx.binding.ButtonBindingBuilder.onButtonClick;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.facade;

/**
 * @author Thomas Spiegl
 */
public class AnimalSearchTabController extends BaseTabController {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

    @FXML
    protected TextInputControl name;
    @FXML
    protected ComboBox<AnimalType> type;
    @FXML

    protected ComboBox<AnimalFamily> family;

    @FXML
    protected TableView<Animal> animalTable;
    @FXML
    protected TableColumn<Animal, String> animalName;
    @FXML
    protected TableColumn<Animal, String> animalType;
    @FXML
    protected TableColumn<Animal, String> animalFamily;

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
                .callAction(new ClickAction<Paginator>() {
                    @Override
                    public Paginator onClick(Paginator paginator) {
                        return paginator.previous();
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(next)
                .callAction(new ClickAction<Paginator>() {
                    @Override
                    public Paginator onClick(Paginator paginator) {
                        return paginator.next();
                    }
                })
                .withParam(paginatorRef).create();

        onButtonClick(save)
                .callAction(new ClickAction() {
                    @Override
                    public Object onClick(Object value) {
                        facade().saveAnimals(tabRef, ActionCompleteCallback.empty);
                        return null;
                    }
                }).create();
    }

    @SuppressWarnings("unchecked")
    private void bindTableColumns() {

        animalName.setCellValueFactory(new PropertyValueFactory<Animal, String>("name"));
        animalName.setCellFactory(TextFieldTableCell.<Animal>forTableColumn());
        animalName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Animal, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Animal, String> t) {
                        int rowNum = t.getTablePosition().getRow();
                        getTabRef().append(String.format("model.animals.rows[%d].name", rowNum)).setValue(t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));
        animalFamily.setCellValueFactory(new PropertyValueFactory<Animal, String>("family"));
    }

}
