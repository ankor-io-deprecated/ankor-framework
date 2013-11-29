package at.irian.ankorsamples.fxrates.client;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankorsamples.fxrates.client.RatesClient.rootRef;

/**
 * @author Thomas Spiegl
 */
public class RatesController implements Initializable {
    public TableView ratesTable;
    public TableColumn symbol;
    public TableColumn ask;
    public TableColumn bid;
    public TableColumn high;
    public TableColumn low;
    public TableColumn direction;
    public TableColumn last;

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RatesController.class);


    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ratesTable.itemsProperty().bind(rootRef().appendPath("rates").fxObservableList());

        symbol.setCellValueFactory(new MapValueFactory<String>("symbol"));
        ask.setCellValueFactory(new MapValueFactory<String>("ask"));
        bid.setCellValueFactory(new MapValueFactory<String>("bid"));
        high.setCellValueFactory(new MapValueFactory<String>("high"));
        low.setCellValueFactory(new MapValueFactory<String>("low"));
        direction.setCellValueFactory(new MapValueFactory<String>("direction"));
        last.setCellValueFactory(new MapValueFactory<String>("last"));
    }
}
