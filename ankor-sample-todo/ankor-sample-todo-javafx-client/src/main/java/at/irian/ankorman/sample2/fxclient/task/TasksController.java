package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankorman.sample2.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

import static at.irian.ankor.fx.binding.ButtonBindingBuilder.onButtonClick;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TasksController extends BaseTabController {

    @FXML public TextField newTodo;
    @FXML public HBox todoCount;
    @FXML public Label todoCountNum;
    @FXML public Button clearCompleted;

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TasksController.class);

    public TasksController(String tabId) {
        super(tabId);
    }

    @Override
    public void initialize() {
        final Ref modelRef = getTabRef().append("model");

        bindValue(modelRef.append("itemsLeft"))
                .toLabel(todoCountNum)
                .createWithin(bindingContext);

        // XXX: Is there a better way to do this? Something like a "visibility variable" maybe?
        RefListeners.addPropChangeListener(modelRef.append("itemsLeft"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                int itemsLeft = Integer.parseInt(changedProperty.<String>getValue());
                todoCount.setVisible(itemsLeft != 0);
            }

        });

        bindValue(modelRef.append("itemsCompleted"))
                .toButton(clearCompleted)
                .createWithin(bindingContext);

        RefListeners.addPropChangeListener(modelRef.append("itemsCompleted"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                int itemsCompleted = Integer.parseInt(changedProperty.<String>getValue());
                clearCompleted.setVisible(itemsCompleted != 0);
            }
        });
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", newTodo.getText());
        getTabRef().append("model").fireAction(new Action("newTodo", params));
        newTodo.clear();
    }

    // XXX
    @Override
    public Ref getTabRef() {
        Ref rootRef = refFactory().rootRef();
        return rootRef.append(String.format("tabsTask.%s", tabId));
    }
}
