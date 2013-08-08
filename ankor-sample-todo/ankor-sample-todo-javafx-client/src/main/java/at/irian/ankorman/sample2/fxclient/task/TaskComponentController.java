package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;

// TODO: Style checkboxes according to TodoMVC
// TODO: Show delete button on hover
public class TaskComponentController extends AnchorPane {

    private Ref modelRef;
    private int index;

    @FXML public CheckBox completedCheckBox;
    @FXML public Button deleteButton;
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
    public void complete(ActionEvent actionEvent) {
        HashMap params = new HashMap<String, Object>();
        params.put("index", index);
        modelRef.fireAction(new Action("toggleTask", params));
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        HashMap params = new HashMap<String, Object>();
        params.put("index", index);
        modelRef.fireAction(new Action("deleteTask", params));
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

    public CheckBox getCompletedCheckBox() {
        return completedCheckBox;
    }

    public void setCompletedCheckBox(CheckBox completedCheckBox) {
        this.completedCheckBox = completedCheckBox;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }
}
