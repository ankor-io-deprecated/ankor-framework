package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.property.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.todosample.viewmodel.task.TaskModel;
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

import java.io.IOException;
import java.util.HashMap;

public class TaskPane extends AnchorPane {

    private Ref itemRef;
    private TaskModel model;
    private int index;

    @FXML public ToggleButton completedButton;
    @FXML public Button deleteButton;
    @FXML public TextField titleTextField;

    private ViewModelProperty<String> title;
    private ViewModelProperty<Boolean> completed;
    private SimpleStringProperty helper = new SimpleStringProperty();

    public TaskPane(Ref itemRef) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.itemRef = itemRef;
        title = new ViewModelProperty<>(itemRef, "title");
        completed = new ViewModelProperty<>(itemRef, "completed");

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
                if (newValue == false) {
                    titleTextField.setEditable(false);
                }
            }
        });

        Bindings.bindBidirectional(completedButton.selectedProperty(), titleTextField.disableProperty());
        completedButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
                if (newValue == false) {
                    titleTextField.getStyleClass().remove("default");
                    titleTextField.getStyleClass().add("strike-through");
                } else {
                    titleTextField.getStyleClass().remove("strike-through");
                    titleTextField.getStyleClass().add("default");
                }
            }
        });
    }

    private void setIndex(int index) {
        this.index = index;
    }

    private void setModel(TaskModel model) {
        this.model = model;
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        HashMap params = new HashMap<String, Object>();
        params.put("index", index);
        itemRef.root().append("model").fire(new Action("deleteTask", params));
    }

    public StringProperty textProperty() {
        return titleTextField.textProperty();
    }

    public BooleanProperty selectedPrperty() {
        return completedButton.selectedProperty();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public void setSelected(boolean selected) {
        this.completedButton.setSelected(selected);
    }

    public boolean isSelected() {
        return this.completedButton.isSelected();
    }

    public TaskModel getModel() {
        return model;
    }

    public void updateContent(TaskModel model, int index) {
        setIndex(index);
        setModel(model);

        helper.unbindBidirectional(textProperty());
        title.unbindBidirectional(helper);
        completed.unbindBidirectional(selectedPrperty());

        title = new ViewModelProperty<>(itemRef, "title");
        completed = new ViewModelProperty<>(itemRef, "completed");
        helper = new SimpleStringProperty();

        helper.bindBidirectional(title);

        textProperty().bindBidirectional(helper);
        selectedPrperty().bindBidirectional(completed);
    }
}
