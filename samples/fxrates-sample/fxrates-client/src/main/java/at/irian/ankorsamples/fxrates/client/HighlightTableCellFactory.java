package at.irian.ankorsamples.fxrates.client;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * @author Thomas Spiegl
 */
public class HighlightTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FormattedTableCellFactory.class);

    @Override
    public TableCell<S, T> call(TableColumn<S, T> stTableColumn) {

        TableCell<S, T> cell = new TableCell<S, T>()
        {
            protected void updateItem(T value, boolean empty)
            {
                super.updateItem(value, empty);
                ((Label)getGraphic()).setText(value != null ? value.toString() : null);
            }
        };
        cell.setGraphic(new HighlightLabel());
        return cell;
    }

    public class HighlightLabel extends Label
    {
        private FadeTransition animation;

        public HighlightLabel()
        {
            animation = new FadeTransition(Duration.millis(4000), this);
            animation.setFromValue(1.0);
            animation.setToValue(1.0);
            animation.setCycleCount(1);
            animation.setAutoReverse(true);

            animation.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setStyle("fx-opacity: 1;");
                    getParent().setStyle("-fx-background-color: transparent;");
                }
            });

            textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> source, String oldValue, String newValue) {
                    if (newValue != null && oldValue != null && oldValue.length() > 0 && !oldValue.equals(newValue)) {
                        if (newValue.compareTo(oldValue) > 0) {
                            getParent().setStyle("-fx-background-color: greenyellow");
                        } else {
                            getParent().setStyle("-fx-background-color: #ffaaaa");
                        }
                        animation.playFromStart();
                    }
                }
            });
        }
    }

}
