package at.irian.ankorman.sample1.fxclient.animal;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.TabIds;
import at.irian.ankorman.sample1.model.model.Tab;
import at.irian.ankorman.sample1.model.model.animal.Animal;
import at.irian.ankorman.sample1.model.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.model.animal.AnimalType;
import at.irian.ankorman.sample1.model.model.animal.search.AnimalSearchModel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.BindingsBuilder.newBinding;
import static at.irian.ankorman.sample1.fxclient.App.application;
import static at.irian.ankorman.sample1.fxclient.App.facade;

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
    private ComboBox<AnimalFamily> family;

    @FXML
    private TableView<Animal> animalTable;
    @FXML
    private TableColumn<Animal, String> animalName;
    @FXML
    private TableColumn<Animal, String> animalType;
    @FXML
    private TableColumn actionCol;

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
                Ref filterRef = getTabRef().sub("model").sub("filter");
                // Bind filter items

                newBinding()
                        .bindValue(filterRef.sub("name"))
                        .toInput(name)
                        .createWithin(bindingContext);

                newBinding()
                        .bindValue(filterRef.sub("type"))
                        .toCombo(type)
                        .withItems(filterRef.sub("families"))
                        .createWithin(bindingContext);

                application().getListenerRegistry().registerRemoteChangeListener(getTabRef().sub("model").sub("animals"), new ChangeListener() {
                    @Override
                    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
                        loadAnimals((List<Animal>) changedProperty.getValue()); // TODO use binding for animals, observable List
                    }
                });
            }
        });

    }

    private Ref getTabRef() {
        Ref rootRef = application().getRefFactory().rootRef();
        return rootRef.sub(String.format("tabs.%s", tabId));
    }

    @SuppressWarnings("unchecked")
    private void loadAnimals(final List<Animal> animals) {

        animalTable.getItems().setAll(animals);

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

        actionCol.setCellFactory(
                new Callback<TableColumn<Animal, String>, TableCell<Animal, String>>() {
                    @Override
                    public TableCell<Animal, String> call(TableColumn<Animal, String> p) {
                        return new ButtonCell();
                    }

                });
    }

    @FXML
    protected void search(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().searchAnimals(getTabRef(), new ActionCompleteCallback() {
            public void onComplete() {
                Tab<AnimalSearchModel> tab = getTabRef().getValue();
                AnimalSearchModel model = tab.getModel();
                loadAnimals(model.getAnimals());
            }
        });
    }

    @FXML
    protected void save(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        facade().saveAnimals(getTabRef(), new ActionCompleteCallback() {
            public void onComplete() {
            }
        });
    }

    @FXML
    protected void edit(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        System.out.println("yes");
    }

    private class ButtonCell extends TableCell<Animal, String> {
        final Button cellButton = new Button("Action");

        ButtonCell(){
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    //Animal animal = animalTable.getItems().get(getTableRow().getIndex());

                    //facade().createAnimalDetailTab();
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if(empty){
                setGraphic(cellButton);
            }
        }
    }
}
