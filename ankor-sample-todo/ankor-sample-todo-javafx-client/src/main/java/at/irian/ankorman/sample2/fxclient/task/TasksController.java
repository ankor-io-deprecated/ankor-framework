package at.irian.ankorman.sample2.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.ClickAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample2.fxclient.BaseTabController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.HashMap;
import java.util.Map;

import static at.irian.ankor.fx.binding.ButtonBindingBuilder.onButtonClick;
import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

public class TasksController extends BaseTabController {

    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;

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
