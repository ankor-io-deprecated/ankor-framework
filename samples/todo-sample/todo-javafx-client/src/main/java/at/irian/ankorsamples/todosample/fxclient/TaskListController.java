package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerSupport;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

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
    public Node footerTop;
    @FXML
    public Node footerBottom;
    private FxRef modelRef;
    private boolean initialized = false;
    //private HashMap<Integer, TaskPane> cache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = FxRefs.refFactory().ref("root");
        FXControllerSupport.init(this, rootRef);
        rootRef.fire(new Action("init"));
    }

    @ChangeListener(pattern = "root")
    public void initialize() {
        initialized = true;

        FxRef rootRef = FxRefs.refFactory().ref("root");
        modelRef = rootRef.appendPath("model");

        todoCountNum.textProperty().bindBidirectional(
                modelRef.appendPath("itemsLeft").<Number>fxProperty(),
                new NumberStringConverter());
        todoCountText.textProperty().bind(modelRef.appendPath("itemsLeftText").<String>fxProperty());

        Property<Boolean> footerVisibility = modelRef.appendPath("footerVisibility").fxProperty();
        footerTop.visibleProperty().bind(footerVisibility);
        footerBottom.visibleProperty().bind(footerVisibility);

        toggleAllButton.visibleProperty().bind(footerVisibility);
        toggleAllButton.selectedProperty().bindBidirectional(modelRef.appendPath("toggleAll").<Boolean>fxProperty());

        clearButton.textProperty().bind(modelRef.appendPath("itemsCompleteText").<String>fxProperty());
        clearButton.visibleProperty().bind(modelRef.appendPath("clearButtonVisibility").<Boolean>fxProperty());

        filterAll.selectedProperty().bindBidirectional(modelRef.appendPath("filterAllSelected").<Boolean>fxProperty());
        filterActive.selectedProperty().bindBidirectional(modelRef.appendPath("filterActiveSelected").<Boolean>fxProperty());
        filterCompleted.selectedProperty().bindBidirectional(modelRef.appendPath("filterCompletedSelected").<Boolean>fxProperty());

        renderTasks(modelRef.appendPath("tasks"));
    }

    @ChangeListener(pattern = "root.model.(tasks)")
    public void renderTasks(FxRef tasksRef) {
        LOG.info("rendering tasks");

        tasksList.getChildren().clear();

        int numTasks = tasksRef.<List>getValue().size();
        for (int index = 0; index < numTasks; index++) {
            FxRef itemRef = tasksRef.appendIndex(index);
            TaskPane node = new TaskPane(itemRef, index);
            tasksList.getChildren().add(node);
        }
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

    @FXML
    public void filterAllClicked(ActionEvent actionEvent) {
        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "all");
    }

    @FXML
    public void filterActiveClicked(ActionEvent actionEvent) {
        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "active");
    }

    @FXML
    public void filterCompletedClicked(ActionEvent actionEvent) {
        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "completed");
    }
}
