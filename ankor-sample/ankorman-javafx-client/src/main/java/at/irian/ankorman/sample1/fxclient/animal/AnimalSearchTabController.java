package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.TabIds;
import at.irian.ankorman.sample1.model.animal.Animal;
import at.irian.ankorman.sample1.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.animal.AnimalType;
import at.irian.ankorman.sample1.model.animal.Paginator;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ButtonBindingBuilder.onButtonClick;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample1.fxclient.App.application;
import static at.irian.ankorman.sample1.fxclient.App.facade;

/**
 * @author Thomas Spiegl
 */
public class AnimalSearchTabController implements Initializable {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);
    @FXML
    private javafx.scene.control.Tab tab;

    @FXML
    private TextInputControl name;
    @FXML
    private ComboBox<AnimalType> type;
    @FXML

    private ComboBox<AnimalFamily> family;

    @FXML
    private TableView<Animal> animalTable;
    @FXML
    private TableColumn<Animal, String> animalName;
    @FXML
    private TableColumn<Animal, String> animalType;
    @FXML
    private TableColumn actionCol;

    @FXML
    private Button previous;
    @FXML
    private Button next;

    private String tabId = TabIds.next();

    private BindingContext bindingContext = new BindingContext();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab.setText(String.format("Animal Search (%s)", tabId));
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                getTabRef().delete();
            }
        });
        facade().createAnimalSearchTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                Ref filterRef = getTabRef().sub("model.filter");
                Ref selItemsRef = getTabRef().sub("model.selectItems");
                Ref rowsRef = getTabRef().sub("model.animals.rows");
                Ref paginatorRef = getTabRef().sub("model.animals.paginator");

                bindTableColumns();

                bindValue(filterRef.sub("name"))
                        .toInput(name)
                        .createWithin(bindingContext);

                bindValue(filterRef.sub("type"))
                        .toCombo(type)
                        .withSelectItems(selItemsRef.sub("types"))
                        .createWithin(bindingContext);

                bindValue(filterRef.sub("family"))
                        .toCombo(family)
                        .withSelectItems(selItemsRef.sub("families"))
                        .createWithin(bindingContext);

                bindValue(rowsRef)
                        .toTable(animalTable)
                        .createWithin(bindingContext);

                onButtonClick(previous)
                        .refresh(paginatorRef)
                        .call(new ClickAction() {
                            @Override
                            public void onClick(Ref valueRef) {
                                Paginator paginator = valueRef.getValue();
                                paginator.previous();
                            }
                        }).bind();

                onButtonClick(next)
                        .refresh(paginatorRef)
                        .call(new ClickAction() {
                            @Override
                            public void onClick(Ref valueRef) {
                                Paginator paginator = valueRef.getValue();
                                paginator.next();
                            }
                        }).bind();
            }
        });

    }

    private Ref getTabRef() {
        Ref rootRef = application().getRefFactory().rootRef();
        return rootRef.sub(String.format("tabs.%s", tabId));
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
                        getTabRef().sub("model").sub(String.format("animals[%d].name", rowNum)).setValue(t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));

    }

    @FXML
    protected void search(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().searchAnimals(getTabRef(), ActionCompleteCallback.empty);
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().saveAnimals(getTabRef(), ActionCompleteCallback.empty);
    }
}
