package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.todosample.viewmodel.task.TaskModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
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

    private Ref modelRef;
    private TaskModel model;

    @FXML public ToggleButton completed;
    @FXML public Button deleteButton;
    @FXML public TextField title;

    private String oldTitle = "";

    public TaskPane(Ref modelRef, TaskModel model) {
        this.modelRef = modelRef;
        this.model = model;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        title.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    title.setEditable(true);
                    title.selectAll();
                }
            }
        });

        title.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    title.setEditable(false);
                    edit();
                }
            }
        });

        title.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
                if (newValue == false) {
                    title.setEditable(false);
                    edit();
                }
            }
        });

        Bindings.bindBidirectional(completed.selectedProperty(), title.disableProperty());
        completed.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean newValue, Boolean oldValue) {
                if (newValue == false) {
                    title.getStyleClass().remove("default");
                    title.getStyleClass().add("strike-through");
                } else {
                    title.getStyleClass().remove("strike-through");
                    title.getStyleClass().add("default");
                }
            }
        });
    }

    private void edit() {
        if (!title.getText().equals(oldTitle)) {
            HashMap params = new HashMap<String, Object>();
            params.put("index", model.getIndex());
            params.put("title", title.getText());
            modelRef.fire(new Action("editTask", params));
            oldTitle = title.getText();
        }
    }

    @FXML
    public void complete(ActionEvent actionEvent) {
        HashMap params = new HashMap<String, Object>();
        params.put("index", model.getIndex());
        modelRef.fire(new Action("toggleTask", params));
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        HashMap params = new HashMap<String, Object>();
        params.put("index", model.getIndex());
        modelRef.fire(new Action("deleteTask", params));
    }

    public StringProperty textProperty() {
        return title.textProperty();
    }

    public BooleanProperty selectedPrperty() {
        return completed.selectedProperty();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        oldTitle = value;
        textProperty().set(value);
    }

    public void setSelected(boolean selected) {
        this.completed.setSelected(selected);
    }

    public boolean isSelected() {
        return this.completed.isSelected();
    }

    public TaskModel getModel() {
        return model;
    }

    public void setModel(TaskModel model) {
        this.model = model;
    }
}
