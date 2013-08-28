package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.fx.binding.property.ViewModelIntegerProperty;
import at.irian.ankor.fx.binding.property.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankor.todosample.fxclient.App;
import at.irian.ankor.todosample.viewmodel.task.TaskModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.controller.FXControllerAnnotationSupport.annotationSupport;
import static at.irian.ankor.todosample.fxclient.App.refFactory;

public class TaskListController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public VBox tasksList;
    @FXML public ToggleButton toggleAllButton;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Label todoCountText;
    @FXML public Button clearButton;

    @FXML public RadioButton filterAll;
    @FXML public RadioButton filterActive;
    @FXML public RadioButton filterCompleted;
    @FXML public ToggleGroup filterToggleGroup;

    @FXML public Node footerTop;
    @FXML public Node footerBottom;

    public TaskListController() {
        annotationSupport().registerChangeListeners(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // XXX: Annotation syntax not supported
        RefListeners.addPropChangeListener(refFactory().ref("root.model"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                initialize(refFactory().ref("root.model"));
            }
        });

        refFactory().ref("root").fire(new Action("init"));
    }

    public void initialize(Ref modelRef) {
        this.modelRef = modelRef;

        ViewModelIntegerProperty itemsLeft = new ViewModelIntegerProperty(modelRef, "itemsLeft");
        ViewModelProperty<String> itemsLeftText = new ViewModelProperty<>(modelRef, "itemsLeftText");
        ViewModelProperty<Boolean> footerVisibility = new ViewModelProperty<>(modelRef, "footerVisibility");
        ViewModelProperty<Boolean> toggleAll = new ViewModelProperty<>(modelRef, "toggleAll");
        ViewModelProperty<String> itemsCompleteText = new ViewModelProperty<>(modelRef, "itemsCompleteText");
        ViewModelProperty<Boolean> clearButtonVisibility = new ViewModelProperty<>(modelRef, "clearButtonVisibility");
        ViewModelProperty<Boolean> filterAllSelected = new ViewModelProperty<>(modelRef, "filterAllSelected");
        ViewModelProperty<Boolean> filterActiveSelected = new ViewModelProperty<>(modelRef, "filterActiveSelected");
        ViewModelProperty<Boolean> filterCompletedSelected = new ViewModelProperty<>(modelRef, "filterCompletedSelected");

        SimpleStringProperty itemsLeftAsString = new SimpleStringProperty();
        Bindings.bindBidirectional(itemsLeftAsString, itemsLeft, new NumberStringConverter());

        todoCountNum.textProperty().bind(itemsLeftAsString);
        todoCountText.textProperty().bind(itemsLeftText);

        footerTop.visibleProperty().bind(footerVisibility);
        footerBottom.visibleProperty().bind(footerVisibility);

        toggleAllButton.visibleProperty().bind(footerVisibility);
        toggleAllButton.selectedProperty().bindBidirectional(toggleAll);

        clearButton.textProperty().bind(itemsCompleteText);
        clearButton.visibleProperty().bind(clearButtonVisibility);

        filterAll.selectedProperty().bindBidirectional(filterAllSelected);
        filterActive.selectedProperty().bindBidirectional(filterActiveSelected);
        filterCompleted.selectedProperty().bindBidirectional(filterCompletedSelected);

        renderTasks(modelRef.appendPath("tasks"));
    }

    @ChangeListener(pattern = "root.model.tasks")
    public void renderTasks(Ref changedProperty) {
        List<LinkedHashMap<String, Object>> tasks = changedProperty.getValue();
        Platform.runLater(new TaskLoader(tasks));
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
    public void toggleAll(ActionEvent actionEvent) {
        modelRef.fire(new Action("toggleAll"));
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fire(new Action("clearTasks"));
    }

    @FXML
    public void openIRIAN(ActionEvent actionEvent) {
        App.getServices().showDocument("http://www.irian.at");
    }

    @FXML
    public void openTodoMVC(ActionEvent actionEvent) {
        App.getServices().showDocument("http://todomvc.com/");
    }

    private HashMap<Integer, TaskPane> cache = new HashMap<>();

    private class TaskLoader implements Runnable {

        private List<LinkedHashMap<String, Object>> tasks;
        public TaskLoader(List<LinkedHashMap<String, Object>> tasks) { this.tasks = tasks; }

        @Override
        public void run() {
            tasksList.getChildren().clear();

            int index = 0;
            for (LinkedHashMap<String, Object> task : tasks) {
                TaskModel model = new TaskModel(task);

                TaskPane node;
                if (cache.get(index) == null) {
                    Ref itemRef = modelRef.appendPath("tasks").appendIndex(index);
                    cache.put(index, new TaskPane(itemRef));
                }
                node = cache.get(index);
                node.updateContent(model, index);

                tasksList.getChildren().add(node);

                index++;
            }
        }
    }
}
