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
import static at.irian.ankorman.sample1.fxclient.App.facade;
import static at.irian.ankorman.sample1.fxclient.App.refFactory;

/**
 * @author Thomas Spiegl
 */
public class AnimalSearchTabController implements Initializable {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);
    @FXML
    protected javafx.scene.control.Tab tab;

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

    private String tabId = TabIds.next();

    private BindingContext bindingContext = new BindingContext();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab.setText(String.format("Animal Search (%s)", tabId));
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                getTabRef().setValue(null);
            }
        });
        facade().createAnimalSearchTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                Ref filterRef = getTabRef().append("model.filter");
                Ref selItemsRef = getTabRef().append("model.selectItems");
                Ref rowsRef = getTabRef().append("model.animals.rows");
                Ref paginatorRef = getTabRef().append("model.animals.paginator");

                bindTableColumns();

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
                            public void onClick(Paginator paginator) {
                                paginator.previous();
                            }
                        })
                        .withParam(paginatorRef).create();

                onButtonClick(next)
                        .callAction(new ClickAction<Paginator>() {
                            @Override
                            public void onClick(Paginator paginator) {
                                paginator.next();
                            }
                        })
                        .withParam(paginatorRef).create();

                onButtonClick(save)
                        .callAction(new ClickAction() {
                            @Override
                            public void onClick(Object value) {
                                facade().saveAnimals(getTabRef(), ActionCompleteCallback.empty);
                            }
                        });

            }
        });

    }

    private Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabs.%s", tabId));
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
                        getTabRef().append("model").append(String.format("animals[%d].name", rowNum)).setValue(t.getNewValue());
                    }
                });

        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));
        animalFamily.setCellValueFactory(new PropertyValueFactory<Animal, String>("family"));
    }

}
