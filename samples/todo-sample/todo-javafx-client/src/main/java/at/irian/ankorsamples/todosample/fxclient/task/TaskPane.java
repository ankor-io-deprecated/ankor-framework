package at.irian.ankorsamples.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.fxref.FxRef;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"PointlessBooleanExpression", "UnusedDeclaration"})
public class TaskPane extends AnchorPane {
    private static Logger LOG = LoggerFactory.getLogger(TaskPane.class);

    private FxRef itemRef;
    private int index;

    @FXML public ToggleButton completedButton;
    @FXML public Button deleteButton;
    @FXML public TextField titleTextField;

    private SimpleStringProperty cursorPositionFix = new SimpleStringProperty();

    public static ClassLoader cachingClassLoader = new MyClassLoader(FXMLLoader.getDefaultClassLoader());

    public TaskPane(FxRef itemRef, int index) {
        this.itemRef = itemRef;
        this.index = index;

        loadFXML();
        setValues();
        bindProperties();
        addEventListeners();
    }

    private void loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
        fxmlLoader.setClassLoader(cachingClassLoader);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValues() {
        titleTextField.textProperty().setValue(itemRef.appendPath("title").<String>getValue());
        completedButton.selectedProperty().setValue(itemRef.appendPath("completed").<Boolean>getValue());
        titleTextField.editableProperty().setValue(itemRef.appendPath("editable").<Boolean>getValue());
    }

    private void bindProperties() {
        titleTextField.textProperty().bindBidirectional(itemRef.appendPath("title").<String>fxProperty());
        completedButton.selectedProperty().bindBidirectional(itemRef.appendPath("completed").<Boolean>fxProperty());
        titleTextField.editableProperty().bindBidirectional(itemRef.appendPath("editable").<Boolean>fxProperty());
    }

    private void addEventListeners() {
        titleTextField.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    titleTextField.setEditable(true);
                    titleTextField.selectAll();
                }
            }
        });

        titleTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    titleTextField.setEditable(false);
                }
            }
        });

        titleTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
                if (newValue == false) {       // todo  newValue <--> oldValue !
                    titleTextField.setEditable(false);
                }
            }
        });

        completedButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
                if (newValue == false) {        // todo  newValue <--> oldValue !
                    titleTextField.getStyleClass().remove("default");
                    titleTextField.getStyleClass().add("strike-through");
                } else {
                    titleTextField.getStyleClass().remove("strike-through");
                    titleTextField.getStyleClass().add("default");
                }
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    @FXML
    public void delete(ActionEvent actionEvent) {
        Map<String, Object> params = new HashMap<>();
        params.put("index", index);
        itemRef.root().appendPath("model").fire(new Action("deleteTask", params));
    }
}
