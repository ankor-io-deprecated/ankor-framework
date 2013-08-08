package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankorman.sample2.viewmodel.task.Filter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bind;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TaskListController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public ListView tasksList;
    @FXML public CheckBox toggleAll;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Button clearButton;

    @FXML public Button filterAll;
    @FXML public Button filterActive;
    @FXML public Button filterCompleted;
    private List<Button> filterButtons = new ArrayList<Button>();

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

        refFactory().rootRef().fireAction(new Action("init"));

    }

    public void initialize() {
        bind(modelRef.append("tasks"))
                .toItemsProperty(tasksList.itemsProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("toggleAll"))
                .toBooleanProperty(toggleAll.selectedProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("itemsLeft"))
                .toStringProperty(todoCountNum.textProperty())
                .forIntegerValue()
                .createWithin(bindingContext);

        bind(modelRef.append("itemsCompleteText"))
                .toStringProperty(clearButton.textProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("clearButtonVisibility"))
                .toBooleanProperty(clearButton.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("footerVisibility"))
                .toBooleanProperty(footerTop.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("footerVisibility"))
                .toBooleanProperty(footerBottom.visibleProperty())
                .createWithin(bindingContext);

        tasksList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView listView) {
                return new TaskComponentListCell();
            }
        });

        setupFilterButtonStyle();
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());

            modelRef.fireAction(new Action("newTask", params));
            newTodo.clear();
        }
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

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fireAction(new Action("clearTasks"));
    }

    // XXX: There's no good way to do this in the viewmodel
    private void setupFilterButtonStyle() {
        filterButtons.add(filterAll);
        filterButtons.add(filterActive);
        filterButtons.add(filterCompleted);

        String filter = modelRef.append("filter").getValue();
        updateFilterButtonStyle(filter);

        RefListeners.addPropChangeListener(modelRef.append("filter"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String prop = changedProperty.getValue();
                updateFilterButtonStyle(prop);
            }
        });
    }

    private void updateFilterButtonStyle(String filterString) {
        Filter filter = Filter.valueOf(filterString);
        for (Button b : filterButtons) {
            b.setStyle("-fx-font-weight: normal;");
        }
        switch (filter) {
            case all: filterAll.setStyle("-fx-font-weight: bold;"); break;
            case active: filterActive.setStyle("-fx-font-weight: bold;"); break;
            case completed: filterCompleted.setStyle("-fx-font-weight: bold;"); break;
        }
    }

    public void toggleAll(ActionEvent actionEvent) {
        modelRef.fireAction(new Action("toggleAll"));
    }

    private class TaskComponentListCell extends ListCell<LinkedHashMap<String, Object>> {
        @Override
        public void updateItem(LinkedHashMap<String, Object> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                String title = (String)item.get("title");
                boolean completed = (boolean)item.get("completed");

                TaskComponentController node = new TaskComponentController(modelRef, getIndex());
                node.setText(title);
                node.getCompletedCheckBox().setSelected(completed);

                setGraphic(node);
            }
        }
    }
}
