package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankorman.sample2.fxclient.BaseTabController;
import at.irian.ankorman.sample2.viewmodel.task.Filter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TasksController extends BaseTabController {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TasksController.class);

    @FXML public TextField newTodo;
    @FXML public HBox todoCount;
    @FXML public Label todoCountNum;
    @FXML public Button clearCompleted;
    @FXML public ListView tasksList;

    @FXML public Button filterAll;
    @FXML public Button filterActive;
    @FXML public Button filterCompleted;

    private List<Button> filterButtons = new ArrayList<Button>();

    private Ref modelRef;

    public TasksController(String tabId) {
        super(tabId);
    }

    @Override
    public void initialize() {
        modelRef = getTabRef().append("model");

        filterButtons.add(filterAll);
        filterButtons.add(filterActive);
        filterButtons.add(filterCompleted);

        String filterString = modelRef.append("filter").getValue();
        Filter filterEnum = Filter.valueOf(filterString);
        setFilterButtonStyle(filterEnum);

        bindValue(modelRef.append("itemsLeft"))
                .toLabel(todoCountNum)
                .createWithin(bindingContext);

        bindValue(modelRef.append("tasks.rows"))
                .toList(tasksList)
                .createWithin(bindingContext);

        // XXX: Is there a better way to do this? Something like a "visibility variable" maybe?
        RefListeners.addPropChangeListener(modelRef.append("itemsLeft"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String prop = changedProperty.getValue();
                int itemsLeft = Integer.parseInt(prop);
                todoCount.setVisible(itemsLeft != 0);
            }
        });

        RefListeners.addPropChangeListener(modelRef.append("filter"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String prop = changedProperty.getValue();
                Filter filterEnum = Filter.valueOf(prop);
                setFilterButtonStyle(filterEnum);
            }
        });

        /*
        TaskComponent test = new TaskComponent();
        test.setText("Dynamically created Task");
        test.getCompleted().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("The button was clicked! 2");
            }
        });

        tasks.getItems().add(test);
        tasks.getItems().add(new TaskComponent());
        tasks.getItems().add(new TaskComponent());
        getTabRef().append("model").append("itemsLeft").setValue(String.valueOf(tasks.getItems().size()));
        */

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                newTodo.requestFocus();
            }
        });
    }

    private void setFilterButtonStyle(Filter filter) {
        for (Button b : filterButtons) {
            b.setStyle("-fx-font-weight: normal;");
        }
        switch (filter) {
            case all: filterAll.setStyle("-fx-font-weight: bold;"); break;
            case active: filterActive.setStyle("-fx-font-weight: bold;"); break;
            case completed: filterCompleted.setStyle("-fx-font-weight: bold;"); break;
        }
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", newTodo.getText());
        modelRef.fireAction(new Action("newTodo", params));
        newTodo.clear();

        String itemsLeftValue = modelRef.append("itemsLeft").getValue();
        int itemsLeft = Integer.parseInt(itemsLeftValue);
        modelRef.append("itemsLeft").setValue(String.valueOf(itemsLeft + 1));
    }

    @FXML
    public void displayAll(ActionEvent actionEvent) {
        modelRef.append("filter").setValue(Filter.all.toString());
    }

    @FXML
    public void displayActive(ActionEvent actionEvent) {
        modelRef.append("filter").setValue(Filter.active.toString());
    }

    @FXML
    public void displayCompleted(ActionEvent actionEvent) {
        modelRef.append("filter").setValue(Filter.completed.toString());
    }

    // XXX
    @Override
    public Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabsTask.%s", tabId));
    }
}
