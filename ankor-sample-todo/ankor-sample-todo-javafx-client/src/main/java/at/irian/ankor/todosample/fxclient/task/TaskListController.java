package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindSubValues;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankor.todosample.fxclient.App.refFactory;

public class TaskListController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public ListView tasksList;
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
        // XXX: tree change listener is needed
        bindSubValues(modelRef.append("tasks"))
                .toProperty(tasksList.itemsProperty())
                .createWithin(bindingContext);

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

        tasksList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView listView) {
                return new TaskComponentListCell();
            }
        });
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

    HashMap<Object, TaskPane> cache = new HashMap<Object, TaskPane>();
    private class TaskComponentListCell extends ListCell<LinkedHashMap<String, Object>> {

        @Override
        public void updateItem(LinkedHashMap<String, Object> item, boolean empty) {
            super.updateItem(item, item == null);
            if (item != null) {
                //Object id = item.get("id");
                int index = getIndex();
                String title = (String)item.get("title");
                boolean completed = (boolean)item.get("completed");

                // XXX: Memory Leak
                if (cache.get(index) == null) {
                    cache.put(index, new TaskPane(modelRef));
                }
                TaskPane node = cache.get(index);

                node.setText(title);
                node.setSelected(completed);
                node.setIndex(index);

                setGraphic(node);
            } else {
                // XXX: Client must know how to handle null values
                setGraphic(null);
            }
        }
    }
}
