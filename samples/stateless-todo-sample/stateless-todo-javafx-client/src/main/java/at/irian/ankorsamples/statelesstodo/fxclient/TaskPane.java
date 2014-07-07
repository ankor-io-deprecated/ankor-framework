package at.irian.ankorsamples.statelesstodo.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.fxref.FxRef;
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
    private String id;

    @FXML public ToggleButton completedButton;
    @FXML public Button deleteButton;
    @FXML public TextField titleTextField;
    
    public TaskPane(FxRef itemRef) {
        this.itemRef = itemRef;
        this.id = itemRef.appendPath("id").getValue();

        loadFXML();
        addEventListeners();
        setValues();
        bindProperties();
    }

    private void loadFXML() {
        // NOTE: Slow, apparently JavaFX has no caching mechanism for this
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
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
        itemRef.appendPath("title").<String>fxProperty().bindBidirectional(titleTextField.textProperty());
        completedButton.selectedProperty().bindBidirectional(itemRef.appendPath("completed").<Boolean>fxProperty());
        titleTextField.editableProperty().bindBidirectional(itemRef.appendPath("editable").<Boolean>fxProperty());
    }

    private void unbindProperties() {
        itemRef.appendPath("title").<String>fxProperty().unbindBidirectional(titleTextField.textProperty());
        completedButton.selectedProperty().unbindBidirectional(itemRef.appendPath("completed").<Boolean>fxProperty());
        titleTextField.editableProperty().unbindBidirectional(itemRef.appendPath("editable").<Boolean>fxProperty());
    }
    
    public void updateRef(FxRef itemRef) {
        unbindProperties();
        this.itemRef = itemRef;
        bindProperties();
    }

    private void addEventListeners() {
        titleTextField.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    titleTextField.setEditable(true);
                    titleTextField.selectAll();
                    //itemRef.root().appendPath("model.editing").setValue(index);
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
        String taskId = itemRef.appendPath("id").getValue();
        params.put("id", taskId);
        itemRef.root().appendPath("model").fire(new Action("deleteTask", params));
    }
    
    public String getTaskId() {
        return id;
    }
}
