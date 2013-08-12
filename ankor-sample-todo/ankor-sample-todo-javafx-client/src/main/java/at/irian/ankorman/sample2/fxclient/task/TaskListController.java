package at.irian.ankorman.sample2.fxclient.task;

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
import java.util.Map;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bind;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TaskListController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public ListView tasksList;
    @FXML public ToggleButton toggleAll;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Button clearButton;

    @FXML public ToggleButton filterAll;
    @FXML public ToggleButton filterActive;
    @FXML public ToggleButton filterCompleted;
    @FXML public ToggleGroup filterTG;

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
                .toProperty(tasksList.itemsProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("toggleAll"))
                .toProperty(toggleAll.selectedProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("itemsLeft"))
                .toProperty(todoCountNum.textProperty())
                .forIntegerValue()
                .createWithin(bindingContext);

        bind(modelRef.append("itemsCompleteText"))
                .toProperty(clearButton.textProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("clearButtonVisibility"))
                .toProperty(clearButton.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("footerVisibility"))
                .toProperty(footerTop.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("footerVisibility"))
                .toProperty(footerBottom.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("footerVisibility"))
                .toProperty(toggleAll.visibleProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("filterAllSelected"))
                .toProperty(filterAll.selectedProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("filterActiveSelected"))
                .toProperty(filterActive.selectedProperty())
                .createWithin(bindingContext);

        bind(modelRef.append("filterCompletedSelected"))
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

            modelRef.fireAction(new Action("newTask", params));
            newTodo.clear();
        }
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fireAction(new Action("clearTasks"));
    }

    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        modelRef.fireAction(new Action("toggleAll"));
    }

    private class TaskComponentListCell extends ListCell<LinkedHashMap<String, Object>> {
        @Override
        public void updateItem(LinkedHashMap<String, Object> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Object id = item.get("id");
                String title = (String)item.get("title");
                boolean completed = (boolean)item.get("completed");

                if (getGraphic() == null) {
                    setGraphic(new TaskPane(modelRef));
                }

                // TODO: editable
                // TODO: add strike-through style class
                TaskPane node = (TaskPane) this.getGraphic();
                node.setIndex(getIndex());
                node.setText(title);
                node.getCompleted().setSelected(completed);

                setGraphic(node);
            }
        }
    }
}
