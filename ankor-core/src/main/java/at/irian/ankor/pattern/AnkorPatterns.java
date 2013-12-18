package at.irian.ankor.pattern;

import at.irian.ankor.change.Change;
import at.irian.ankor.delay.TaskRequestEvent;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.viewmodel.RefAware;
import at.irian.ankor.viewmodel.ViewModels;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public final class AnkorPatterns {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorPatterns.class);

    private static final CustomSource STATIC_SOURCE = new CustomSource(AnkorPatterns.class);

    private AnkorPatterns() {}

    public static void runLater(Ref property, Runnable task) {
        getEventDispatcherFor(property).dispatch(new TaskRequestEvent(STATIC_SOURCE,
                                                                      task));
    }

    private static EventDispatcher getEventDispatcherFor(Ref property) {
        return property.context().modelContext().getEventDispatcher();
    }


    public static void changeValueLater(Ref property, Object newValue) {
        applyChangeLater(property, Change.valueChange(newValue));
    }

    public static void deleteItemLater(Ref property, Object key) {
        applyChangeLater(property, Change.deleteChange(key));
    }

    public static void insertItemLater(Ref property, int idx, Object value) {
        applyChangeLater(property, Change.insertChange(idx, value));
    }

    public static void replaceItemsLater(Ref property, int fromIdx, Collection newElements) {
        applyChangeLater(property, Change.replaceChange(fromIdx, newElements));
    }

    private static void applyChangeLater(final Ref property, final Change change) {
        runLater(property, new Runnable() {
            @Override
            public void run() {
                ((RefImplementor) property).apply(STATIC_SOURCE, change);
            }
        });
    }


    public static void initViewModel(Object viewModelObject, Ref viewModelRef) {
        ViewModels.invokePostProcessorsOn(viewModelObject, viewModelRef);
    }

    public static void initViewModel(RefAware viewModelObject) {
        Ref ref = viewModelObject.getRef();
        if (ref == null) {
            throw new IllegalArgumentException("View model object " + viewModelObject + " not properly initialized - no Ref assigned yet.");
        }
        initViewModel(viewModelObject, ref);
    }
}
