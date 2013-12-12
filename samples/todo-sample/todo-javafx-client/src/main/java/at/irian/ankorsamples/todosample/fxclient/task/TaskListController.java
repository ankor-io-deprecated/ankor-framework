package at.irian.ankorsamples.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.controller.FXControllerAnnotationSupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.fxclient.App;
import at.irian.ankorsamples.todosample.viewmodel.TaskModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.*;

import static at.irian.ankorsamples.todosample.fxclient.App.refFactory;

@SuppressWarnings("UnusedParameters")
public class TaskListController implements Initializable {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);
    @FXML
    public VBox tasksList;
    @FXML
    public ToggleButton toggleAllButton;
    @FXML
    public TextField newTodo;
    @FXML
    public Label todoCountNum;
    @FXML
    public Label todoCountText;
    @FXML
    public Button clearButton;
    @FXML
    public RadioButton filterAll;
    @FXML
    public RadioButton filterActive;
    @FXML
    public RadioButton filterCompleted;
    @FXML
    public ToggleGroup filterToggleGroup;
    @FXML
    public Node footerTop;
    @FXML
    public Node footerBottom;
    private FxRef modelRef;
    private boolean initialized = false;
    private HashMap<Integer, TaskPane> cache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = refFactory().ref("root");
        rootRef.fire(new Action("init"));
        FXControllerAnnotationSupport.scan(rootRef, this);
    }

    @ChangeListener(pattern = "root")
    public void initialize() {
        initialized = true;

        FxRef rootRef = refFactory().ref("root");
        modelRef = rootRef.appendPath("model");
        FxRef tasksRef = modelRef.appendPath("tasks");

        SimpleStringProperty itemsLeftAsString = new SimpleStringProperty();
        Bindings.bindBidirectional(itemsLeftAsString,
                                   modelRef.appendPath("itemsLeft").<Number>fxProperty(),
                                   new NumberStringConverter());

        todoCountNum.textProperty().bind(itemsLeftAsString);
        todoCountText.textProperty().bind(modelRef.appendPath("itemsLeftText").<String>fxProperty());

        Property<Boolean> footerVisibility = modelRef.appendPath("footerVisibility").<Boolean>fxProperty();
        footerTop.visibleProperty().bind(footerVisibility);
        footerBottom.visibleProperty().bind(footerVisibility);

        toggleAllButton.visibleProperty().bind(modelRef.appendPath("footerVisibility").<Boolean>fxProperty());
        toggleAllButton.selectedProperty().bindBidirectional(modelRef.appendPath("toggleAll").<Boolean>fxProperty());

        clearButton.textProperty().bind(modelRef.appendPath("itemsCompleteText").<String>fxProperty());
        clearButton.visibleProperty().bind(modelRef.appendPath("clearButtonVisibility").<Boolean>fxProperty());

        filterAll.selectedProperty().bindBidirectional(modelRef.appendPath("filterAllSelected").<Boolean>fxProperty());
        filterActive.selectedProperty().bindBidirectional(modelRef.appendPath("filterActiveSelected").<Boolean>fxProperty());
        filterCompleted.selectedProperty().bindBidirectional(modelRef.appendPath("filterCompletedSelected").<Boolean>fxProperty());

        renderTasks(tasksRef);
    }

    @ChangeListener(pattern = "root.model.(tasks)")
    public void renderTasks(Ref tasksRef) {
        List<LinkedHashMap<String, Object>> tasks = tasksRef.getValue();
        Platform.runLater(new TaskLoaderRunnable(tasks));      // todo  runLater still necessary?
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        if (!initialized) throw new IllegalStateException("Not initialized! (Response from server not received)");

        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());

            modelRef.fire(new Action("newTask", params));
            newTodo.clear();
        }
    }

    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        if (!initialized) throw new IllegalStateException("Not initialized! (Response from server not received)");

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("toggleAll", toggleAllButton.selectedProperty().get());

        modelRef.fire(new Action("toggleAll", params));
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        if (!initialized) throw new IllegalStateException("Not initialized! (Response from server not received)");
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

    private class TaskLoaderRunnable implements Runnable {

        private List<LinkedHashMap<String, Object>> tasks;

        public TaskLoaderRunnable(List<LinkedHashMap<String, Object>> tasks) {
            this.tasks = tasks;
        }

        @Override
        public void run() {
            tasksList.getChildren().clear();

            int index = 0;
            try {

                for (LinkedHashMap<String, Object> task : tasks) {
                    TaskModel model = new TaskModel(task);

                    TaskPane node;
                    if (cache.get(index) == null) {
                        FxRef itemRef = modelRef.appendPath("tasks").appendIndex(index);
                        cache.put(index, new TaskPane(itemRef));
                    }
                    node = cache.get(index);
                    node.updateContent(model, index);

                    tasksList.getChildren().add(node);

                    index++;
                }
            } catch (ConcurrentModificationException ignored) {
                // XXX: This happens when there are a lot of changes in the list, but it shouldn't be a problem,
                // since the change should have triggered another rendering of the list
                LOG.error("Modification of tasks list while drawing..");
            }
        }
    }
}
