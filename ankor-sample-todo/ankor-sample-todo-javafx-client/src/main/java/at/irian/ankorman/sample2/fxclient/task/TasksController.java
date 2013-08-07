package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankorman.sample2.fxclient.BaseTabController;
import at.irian.ankorman.sample2.viewmodel.task.Filter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TasksController extends BaseTabController {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TasksController.class);

    @FXML public ListView tasksList;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Button clearCompleted;

    @FXML public Button filterAll;
    @FXML public Button filterActive;
    @FXML public Button filterCompleted;

    @FXML public Node footerTop;
    @FXML public Node footerBottom;

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

        bindValue(modelRef.append("tasks.rows"))
                .toList(tasksList)
                .createWithin(bindingContext);


        bindValue(modelRef.append("itemsLeft"))
                .forIntegerValue()
                .toLabel(todoCountNum)
                .createWithin(bindingContext);

        int itemsLeft = modelRef.append("itemsLeft").getValue();
        setFooterVisibility(itemsLeft);

        // XXX: Is there a better way to do this?
        RefListeners.addPropChangeListener(modelRef.append("itemsLeft"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                int itemsLeft = changedProperty.getValue();
                setFooterVisibility(itemsLeft);
            }
        });

        String filterValue = modelRef.append("filter").getValue();
        Filter filterEnum = Filter.valueOf(filterValue);
        setFilterButtonStyle(filterEnum);

        RefListeners.addPropChangeListener(modelRef.append("filter"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String prop = changedProperty.getValue();
                Filter filterEnum = Filter.valueOf(prop);
                setFilterButtonStyle(filterEnum);
            }
        });

        tasksList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView listView) {
                return new TaskComponentListCell();
            }
        });

        // TODO: Completing tasks listener
    }

    private void setFooterVisibility(int itemsLeft) {
        footerTop.setVisible(itemsLeft != 0);
        footerBottom.setVisible(itemsLeft != 0);
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
        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());
            params.put("completed", false);

            modelRef.fireAction(new Action("newTodo", params));
            newTodo.clear();

            /*
            String itemsLeftValue = modelRef.append("itemsLeft").getValue();
            int itemsLeft = Integer.parseInt(itemsLeftValue);
            modelRef.append("itemsLeft").setValue(String.valueOf(itemsLeft + 1));
            */

            // XXX: For latency compensation
            //tasksList.getItems().add(params);
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

    private static class TaskComponentListCell extends ListCell<LinkedHashMap<String, Object>> {
        @Override
        public void updateItem(LinkedHashMap<String, Object> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                String title = (String)item.get("title");
                boolean completed = (boolean)item.get("completed");

                TaskComponent node = new TaskComponent();
                node.setText(title);
                node.getCompleted().setSelected(completed);

                setGraphic(node);
            }
        }
    }

    // XXX
    @Override
    public Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabsTask.%s", tabId));
    }

}
