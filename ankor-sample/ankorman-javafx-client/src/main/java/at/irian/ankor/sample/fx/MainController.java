package at.irian.ankor.sample.fx;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.sample.fx.app.ActionCompleteCallback;
import at.irian.ankor.sample.fx.app.App;
import at.irian.ankor.sample.fx.binding.BindingContext;
import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.model.AnimalType;
import at.irian.ankor.sample.fx.view.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.sample.fx.app.ServiceFacade.service;
import static at.irian.ankor.sample.fx.binding.ModelBindings.bind;

/**
 * @author Thomas Spiegl
 */
public class MainController implements Initializable {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainController.class);

    @FXML
    private Text userName;
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

    private String tabId = "A";

    private BindingContext bindingContext = new BindingContext();

    public void initialize(URL url, ResourceBundle resourceBundle) {

        initApplication();

        loadAnimals();
    }

    private void createTab() {
        service().createAnimalSearchTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                Ref rootRef = App.getApplication().getRefFactory().rootRef();

                // Bind Filter
                Ref tabRef = rootRef.sub(String.format("tabs.%s", tabId));
                bind(tabRef.sub("model.filter.name"), name.textProperty(), bindingContext);
                bind(tabRef.sub("model.filter.type"), type.textProperty(), bindingContext);
            }
        });
    }

    private void initApplication() {
        service().initApplication(new ActionCompleteCallback() {

            public void onComplete() {
                Ref rootRef = App.getApplication().getRefFactory().rootRef();
                ViewModel viewModel = rootRef.getValue();

                userName.setText(viewModel.getUserName());

                createTab();
            }
        });
    }

    private void loadAnimals() {
        animalName.setCellValueFactory(new PropertyValueFactory<Animal, String>("name"));
        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));
        List<Animal> animals = new ArrayList<Animal>();
        animals.add(new Animal("Trout", AnimalType.Fish));
        animals.add(new Animal("Salmon", AnimalType.Fish));
        animals.add(new Animal("Pike", AnimalType.Fish));
        animalTable.getItems().setAll(animals);
    }

    @FXML
    protected void search(@SuppressWarnings("UnusedParameters") ActionEvent event) {
    }
}
