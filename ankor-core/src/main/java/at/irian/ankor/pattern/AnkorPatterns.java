package at.irian.ankor.pattern;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeRequestEvent;
import at.irian.ankor.delay.TaskRequestEvent;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelSupport;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public final class AnkorPatterns {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorPatterns.class);

    private AnkorPatterns() {}

    public static void runLater(Ref property, Runnable task) {
        getEventDispatcherFor(property).dispatch(new TaskRequestEvent(property, task));
    }

    public static void changeValueLater(Ref property, Object newValue) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(property, Change.valueChange(newValue)));
    }

    public static void deleteItemLater(Ref property, Object key) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(property, Change.deleteChange(key)));
    }

    public static void insertItemLater(Ref property, int idx, Object value) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(property, Change.insertChange(idx, value)));
    }


    private static EventDispatcher getEventDispatcherFor(Ref property) {
        return property.context().modelContext().getEventDispatcher();
    }


    public static void initViewModel(Object viewModelObject, Ref viewModelRef) {
        ViewModelSupport.invokePostProcessorsOn(viewModelObject, viewModelRef);
    }

}
