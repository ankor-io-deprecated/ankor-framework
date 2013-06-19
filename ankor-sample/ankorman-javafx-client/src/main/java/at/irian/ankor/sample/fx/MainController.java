package at.irian.ankor.sample.fx;

import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.ref.RootRef;
import at.irian.ankor.sample.fx.app.ActionCompleteCallback;
import at.irian.ankor.sample.fx.binding.BindingContext;
import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.view.AnimalSearchModel;
import at.irian.ankor.sample.fx.view.RootModel;
import at.irian.ankor.sample.fx.view.Tab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.sample.fx.App.application;
import static at.irian.ankor.sample.fx.App.facade;
import static at.irian.ankor.sample.fx.binding.ModelBindings.bind;

/**
 * @author Thomas Spiegl
 */
public class MainController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainController.class);

    @FXML
    public Text serverStatus;
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
    }

    private void createTab() {
        facade().createAnimalSearchTab(tabId, new ActionCompleteCallback() {

            public void onComplete() {
                ModelRef filterRef = getTabRef().sub("model").sub("filter");

                // Bind Filter
                bind(filterRef.sub("name"), name.textProperty(), bindingContext);
                bind(filterRef.sub("type"), type.textProperty(), bindingContext);
            }
        });
    }

    private ModelRef getTabRef() {
        RootRef rootRef = application().getRefFactory().rootRef();
        return rootRef.sub(String.format("tabs.%s", tabId));
    }

    private void initApplication() {
        facade().initApplication(new ActionCompleteCallback() {

            public void onComplete() {
                RootRef rootRef = application().getRefFactory().rootRef();
                RootModel rootModel = rootRef.getValue();

                userName.setText(rootModel.getUserName());

                bind(rootRef.sub("serverStatus"), serverStatus.textProperty(), bindingContext);

                createTab();
            }
        });
    }

    private void loadAnimals(List<Animal> animals) {
        animalName.setCellValueFactory(new PropertyValueFactory<Animal, String>("name"));
        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));
        animalTable.getItems().setAll(animals);
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
}
