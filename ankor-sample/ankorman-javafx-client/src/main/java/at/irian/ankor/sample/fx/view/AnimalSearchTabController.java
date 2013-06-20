package at.irian.ankor.sample.fx.view;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.NilValue;
import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.sample.fx.server.model.Animal;
import at.irian.ankor.sample.fx.view.model.AnimalSearchTab;
import at.irian.ankor.sample.fx.view.model.Tab;
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

import static at.irian.ankor.fx.binding.ModelBindings.bind;
import static at.irian.ankor.sample.fx.App.application;
import static at.irian.ankor.sample.fx.App.facade;

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
    private TextInputControl type;
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
                getTabRef().setValue(NilValue.instance());
            }
        });
        facade().createAnimalSearchTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                Ref filterRef = getTabRef().sub("model").sub("filter");

                // Bind Filter
                bind(filterRef.sub("name"), name.textProperty(), bindingContext);
                bind(filterRef.sub("type"), type.textProperty(), bindingContext);
            }
        });
    }

    private Ref getTabRef() {
        Ref rootRef = application().getRefFactory().rootRef();
        return rootRef.sub(String.format("tabs.%s", tabId));
    }

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
                Tab<AnimalSearchTab> tab = getTabRef().getValue();
                AnimalSearchTab model = tab.getModel();
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
                    Animal animal = animalTable.getItems().get(getTableRow().getIndex());

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
