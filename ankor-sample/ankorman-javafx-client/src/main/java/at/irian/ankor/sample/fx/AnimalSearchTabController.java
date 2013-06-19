package at.irian.ankor.sample.fx;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.sample.fx.app.ActionCompleteCallback;
import at.irian.ankor.sample.fx.binding.BindingContext;
import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.view.AnimalSearchModel;
import at.irian.ankor.sample.fx.view.Tab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static at.irian.ankor.sample.fx.App.application;
import static at.irian.ankor.sample.fx.App.facade;
import static at.irian.ankor.sample.fx.binding.ModelBindings.bind;

/**
 * @author Thomas Spiegl
 */
public class AnimalSearchTabController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchTabController.class);

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

    static class TabIds {
        private static AtomicInteger current = new AtomicInteger(0);

        static String next() {
            return "A" + current.incrementAndGet();
        }
    }

    private BindingContext bindingContext = new BindingContext();

    public void initialize(URL url, ResourceBundle resourceBundle) {
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
