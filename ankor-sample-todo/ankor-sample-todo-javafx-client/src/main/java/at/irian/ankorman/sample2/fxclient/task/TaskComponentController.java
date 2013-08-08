package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.HashMap;

// TODO: Style checkboxes according to TodoMVC
// TODO: Show delete button on hover
public class TaskComponentController extends HBox {

    private Ref modelRef;
    private int index;

    @FXML public CheckBox completed;
    @FXML public Label title;


    public TaskComponentController(Ref modelRef, int index) {
        this.modelRef = modelRef;
        this.index = index;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("task.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void completed(ActionEvent actionEvent) {
        //modelRef.append(String.format("tasks[%s].completed", index)).setValue(completed.isSelected());

        HashMap params = new HashMap<String, Object>();
        params.put("index", index);
        modelRef.fireAction(new Action("completeTask", params));
    }

    public StringProperty textProperty() {
        return title.textProperty();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public CheckBox getCompleted() {
        return completed;
    }

    public void setCompleted(CheckBox completed) {
        this.completed = completed;
    }
}
