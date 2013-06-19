package at.irian.ankor.sample.fx.view;

import at.irian.ankor.core.listener.NilValue;
import at.irian.ankor.core.ref.Ref;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

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

        animalName.setCellValueFactory(new PropertyValueFactory<Animal, String>("name"));
        //animalName.setCellFactory(cellFactory);
        animalName.setCellFactory(TextFieldTableCell.<Animal>forTableColumn());
        animalName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Animal, String>>() {
                    @Override public void handle(TableColumn.CellEditEvent<Animal, String> t) {
                        Animal animal = t.getTableView().getItems().get(t.getTablePosition().getRow());
                        animal.setName(t.getNewValue());
                        getTabRef().sub("model").sub("animals").setValue(animals);
                    }
                });

        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));


        animalTable.getItems().setAll(animals);
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
}
