package at.irian.ankorsamples.statelesstodo.fxclient;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerSupport;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.*;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = FxRefs.refFactory().ref("root");
        FXControllerSupport.init(this, rootRef);
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

    /**
     * Render the task list, updating the UI only if the list actually changed.
     * 
     * @param tasksRef A @{link FxRef} to the list of tasks.
     */
    @ChangeListener(pattern = "root.model.(tasks)")
    public void renderTasks(FxRef tasksRef) {
        LOG.info("rendering tasks");

        // get the list of nodes of the current items
        ObservableList<Node> children = tasksList.getChildren();
        children.removeAll(Collections.singleton(null)); // remove null values
        
        // since the indices shift with every addition/deletion keep a copy
        List<Node> childrenCopy = new ArrayList<>(children);

        // the ids of the current items
        List<String> currentIds = new ArrayList<>(children.size());
        for (Node n : children) {
            currentIds.add(((TaskPane) n).getTaskId());
        }

        // the ids of the new items
        List<String> newIds = new ArrayList<>(children.size());
        for (int i = 0; i < tasksRef.<List>getValue().size(); i++) {
            newIds.add(tasksRef.appendIndex(i).appendPath("id").<String>getValue());
        }
        
        // get a set of matches between the ids in the current and the new list
        Set<Match> matchSet = getMatchSet(currentIds, newIds);

        // update the UI based on these matches
        for (Match m : matchSet) {
            m.update(tasksRef, childrenCopy);
        }
    }

    /**
     * Get a set of matches between the ids in the current and the new list
     * 
     * @param currentIds a ordered list of ids, representing the current state in the UI
     * @param newIds a ordered list of ids, representing the new state in the UI
     * @return A set of matches between the ids in the current and the new list
     */
    private Set<Match> getMatchSet(List<String> currentIds, List<String> newIds) {
        Set<Match> matchList = new LinkedHashSet<>(currentIds.size() + newIds.size());

        for (int i = 0; i < currentIds.size(); i++) {
            String id = currentIds.get(i);
            int indexOfId = newIds.indexOf(id);
            Match m = getMatchByIndex(i, indexOfId);
            matchList.add(m);
        }

        for (int i = 0; i < newIds.size(); i++) {
            String id = newIds.get(i);
            int indexOfId = currentIds.indexOf(id);
            Match m = getMatchByIndex(indexOfId, i);
            matchList.add(m);
        }

        return matchList;
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());

            modelRef.fire(new Action("newTask", params));
            newTodo.clear();
        }
    }

    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("toggleAll", toggleAllButton.selectedProperty().get());
        modelRef.fire(new Action("toggleAll", params));
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        modelRef.fire(new Action("clearTasks"));
    }

    @FXML
    public void openIRIAN(ActionEvent actionEvent) {
        throw new UnsupportedOperationException();
    }

    @FXML
    public void openTodoMVC(ActionEvent actionEvent) {
        throw new UnsupportedOperationException();
    }

    @FXML
    public void filterAllClicked(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "all");
    }

    @FXML
    public void filterActiveClicked(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "active");
    }

    @FXML
    public void filterCompletedClicked(ActionEvent actionEvent) {
        if (!initialized) {
            LOG.error("Not initialized! (Response from server not received)");
            return;
        }

        AnkorPatterns.changeValueLater(modelRef.appendPath("filter"), "completed");
    }

    /**
     * Get the correct {@link Match} based on the indices of an item in two lists.
     * 
     * @param currentIndex the index of the item in the current list
     * @param newIndex the index of the item in the new list
     * @return the correct {@link Match} based on the indices
     */
    private Match getMatchByIndex(int currentIndex, int newIndex) {
        if (currentIndex >= 0 && newIndex >= 0) {
            if (currentIndex == newIndex) {
                return new PassThroughMatch(currentIndex);
            } else {
                return new MoveMatch(currentIndex, newIndex);
            }
        } else if (currentIndex < 0) {
            return new AddMatch(newIndex);
        } else if (newIndex < 0) {
            return new RemoveMatch(currentIndex);
        }

        throw new RuntimeException("Not possible");
    }

    /**
     * Details the relation of a single item between two lists.
     *
     * The item can be present only in the current list: {@link RemoveMatch}
     * The item can be present only in the new list: {@link AddMatch}
     * The item can have kept its index: {@link PassThroughMatch}
     * The item can have a different index in both lists: {@link MoveMatch}
     * 
     * Should only be created via {@link #getMatchByIndex(int, int)}
     */
    private abstract class Match {
        protected final int currentIndex;
        protected final int newIndex;

        /**
         * @param currentIndex The index of the item in the current list, -1 if not present
         * @param newIndex The index of the item in the new list, -1 if not present
         */
        public Match(int currentIndex, int newIndex) {
            this.currentIndex = currentIndex;
            this.newIndex = newIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Match)) return false;

            Match match = (Match) o;

            if (currentIndex != match.currentIndex) return false;
            if (newIndex != match.newIndex) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = currentIndex;
            result = 31 * result + newIndex;
            return result;
        }
        
        /**
         * Updates the UI.
         *
         * Since this will change the indices, a copy of the nodes is required.
         *
         * @param tasksRef a {@link FxRef} to the tasks
         * @param childrenCopy a list of the current nodes in the UI, not effected by previous changes
         */
        public abstract void update(FxRef tasksRef, List<Node> childrenCopy);
    }

    /**
     * Both items have the same index in both lists
     */
    private class PassThroughMatch extends Match {
        public PassThroughMatch(int index) {
            super(index, index);
        }

        @Override
        public void update(FxRef tasksRef, List<Node> childrenCopy) {
        }
    }

    /**
     * The item is only present in the new list
     */
    private class AddMatch extends Match {
        
        public AddMatch(int newIndex) {
            super(-1, newIndex);
        }

        /**
         * @return the index of the task to be added
         */
        public int getAddIndex() {
            return newIndex;
        }
        
        @Override
        public void update(FxRef tasksRef, List<Node> childrenCopy) {
            tasksList.getChildren().add(getAddIndex(), new TaskPane(tasksRef.appendIndex(getAddIndex())));
        }

    }

    /**
     * The item has a different index in the new list
     */
    private class MoveMatch extends Match {

        public MoveMatch(int index1, int index2) {
            super(index1, index2);
        }

        /**
         * @return the index in the current list
         */
        public int getFromIndex() {
            return currentIndex;
        }

        /**
         * @return the index in the new list
         */
        public int getToIndex() {
            return newIndex;
        }
        
        @Override
        public void update(FxRef tasksRef, List<Node> childrenCopy) {
            ((TaskPane) childrenCopy.get(getFromIndex())).updateRef(tasksRef.appendIndex(getToIndex()));
        }

    }

    /**
     * The item is not present in the new list
     */
    private class RemoveMatch extends Match {

        public RemoveMatch(int currentIndex) {
            super(currentIndex, -1);
        }

        /**
         * @return the index of the task to be removed
         */
        public int getRemoveIndex() {
            return currentIndex;
        }
        
        @Override
        public void update(FxRef tasksRef, List<Node> childrenCopy) {
            tasksList.getChildren().remove(childrenCopy.get(getRemoveIndex()));
        }

    }
}
