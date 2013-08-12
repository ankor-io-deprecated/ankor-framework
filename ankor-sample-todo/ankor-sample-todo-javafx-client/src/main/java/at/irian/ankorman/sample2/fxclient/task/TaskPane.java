package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;

// TODO: Text strike-through on complete
public class TaskPane extends AnchorPane {

    private Ref modelRef;
    private int index = -1;

    @FXML public ToggleButton completed;
    @FXML public Button deleteButton;
    @FXML public Label title;


    public TaskPane(Ref modelRef) {
        this.modelRef = modelRef;

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

    public ToggleButton getCompleted() {
        return completed;
    }

    public void setCompleted(ToggleButton completed) {
        this.completed = completed;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
