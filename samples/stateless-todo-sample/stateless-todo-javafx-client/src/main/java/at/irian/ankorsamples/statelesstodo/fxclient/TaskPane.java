package at.irian.ankorsamples.statelesstodo.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.pattern.AnkorPatterns;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TaskPane extends AnchorPane {
    //private static Logger LOG = LoggerFactory.getLogger(TaskPane.class);

    private FxRef itemRef;
    private String id;
    private int index;

    @FXML public ToggleButton completedButton;
    @FXML public Button deleteButton;
    @FXML public TextField titleTextField;
    
    public TaskPane(int index, FxRef itemRef) {
        this.itemRef = itemRef;
        this.id = itemRef.appendPath("id").getValue();
        this.index = index;

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
    }
    
    private void bindProperties() {
        itemRef.appendPath("title").<String>fxProperty().bindBidirectional(titleTextField.textProperty());
        itemRef.appendPath("completed").<Boolean>fxProperty().bindBidirectional(completedButton.selectedProperty());
    }

    private void unbindProperties() {
        itemRef.appendPath("title").<String>fxProperty().unbindBidirectional(titleTextField.textProperty());
        itemRef.appendPath("completed").<Boolean>fxProperty().unbindBidirectional(completedButton.selectedProperty());
    }
    
    public void updateRef(int index, FxRef itemRef) {
        unbindProperties();
        this.itemRef = itemRef;
        this.index = index;
        bindProperties();
    }

    private void addEventListeners() {
        titleTextField.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    titleTextField.setEditable(true);
                    titleTextField.selectAll();
                    AnkorPatterns.changeValueLater(itemRef.root().appendPath("model.editing"), index);
                }
            }
        });

        titleTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    titleTextField.setEditable(false);
                    AnkorPatterns.changeValueLater(itemRef.root().appendPath("model.editing"), -1);
                }
            }
        });
        
        titleTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean isFocused) {
                if (isFocused) {
                    // Having a 2-way bind while editing causes the caret position to reset
                    // Therefore just using a regular bind
                    titleTextField.textProperty().unbindBidirectional(itemRef.appendPath("title").<String>fxProperty());
                    itemRef.appendPath("title").<String>fxProperty().bind(titleTextField.textProperty());
                } else {
                    titleTextField.setEditable(false);
                    AnkorPatterns.changeValueLater(itemRef.root().appendPath("model.editing"), -1);
                    
                    // resetting to 2-way bind
                    itemRef.appendPath("title").<String>fxProperty().unbind();
                    titleTextField.textProperty().bindBidirectional(itemRef.appendPath("title").<String>fxProperty());
                }
            }
        });

        completedButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean isSelected) {
                if (isSelected) {
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
