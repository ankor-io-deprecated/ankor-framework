package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankor.todosample.fxclient.App.refFactory;

public class TaskListController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public VBox tasksList;
    @FXML public ToggleButton toggleAll;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Button clearButton;

    @FXML public RadioButton filterAll;
    @FXML public RadioButton filterActive;
    @FXML public RadioButton filterCompleted;
    @FXML public ToggleGroup filterToggleGroup;

    @FXML public Node footerTop;
    @FXML public Node footerBottom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modelRef = refFactory().rootRef().append("model");

        // TODO: @ActionListener syntax
        RefListeners.addPropChangeListener(modelRef, new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                initialize();
            }
        });

        refFactory().rootRef().fire(new Action("init"));
    }

    public void initialize() {
        RefListeners.addPropChangeListener(modelRef.append("tasks"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                List<LinkedHashMap<String, Object>> tasks = changedProperty.getValue();
                Platform.runLater(new TaskLoader(tasks));
            }
        });

        bindValue(modelRef.append("toggleAll"))
                .toProperty(toggleAll.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("itemsLeft"))
                .toProperty(todoCountNum.textProperty())
                .forIntegerValue()
                .createWithin(bindingContext);

        bindValue(modelRef.append("itemsCompleteText"))
                .toProperty(clearButton.textProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("clearButtonVisibility"))
                .toProperty(clearButton.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(footerTop.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(footerBottom.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(toggleAll.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterAllSelected"))
                .toProperty(filterAll.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterActiveSelected"))
                .toProperty(filterActive.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterCompletedSelected"))
                .toProperty(filterCompleted.selectedProperty())
                .createWithin(bindingContext);
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());

            modelRef.fire(new Action("newTask", params));
            newTodo.clear();
        }
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fire(new Action("clearTasks"));
    }

    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        modelRef.fire(new Action("toggleAll"));
    }

    private class TaskLoader implements Runnable {

        List<LinkedHashMap<String, Object>> tasks;
        public TaskLoader(List<LinkedHashMap<String, Object>> tasks) { this.tasks = tasks; }

        @Override
        public void run() {
            tasksList.getChildren().clear();

            int i = 0;
            for (LinkedHashMap<String, Object> task : tasks) {
                TaskPane node = new TaskPane(modelRef);
                node.setIndex(i++);
                node.setText((String)task.get("title"));
                node.setSelected((boolean)task.get("completed"));
                tasksList.getChildren().add(node);
            }
        }
    }
}
