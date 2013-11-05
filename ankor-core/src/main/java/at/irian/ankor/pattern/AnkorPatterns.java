package at.irian.ankor.pattern;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeRequestEvent;
import at.irian.ankor.delay.TaskRequestEvent;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.RefAware;
import at.irian.ankor.viewmodel.ViewModelSupport;

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


    @Deprecated  // todo: static source ok?
    public static void changeValueLater(Ref property, Object newValue) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(STATIC_SOURCE, property, Change.valueChange(newValue)));
    }

    @Deprecated  // todo: static source ok?
    public static void deleteItemLater(Ref property, Object key) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(STATIC_SOURCE, property, Change.deleteChange(key)));
    }

    @Deprecated  // todo: static source ok?
    public static void insertItemLater(Ref property, int idx, Object value) {
        getEventDispatcherFor(property).dispatch(new ChangeRequestEvent(STATIC_SOURCE, property, Change.insertChange(idx, value)));
    }


    private static EventDispatcher getEventDispatcherFor(Ref property) {
        return property.context().modelContext().getEventDispatcher();
    }


    public static void initViewModel(Object viewModelObject, Ref viewModelRef) {
        ViewModelSupport.invokePostProcessorsOn(viewModelObject, viewModelRef);
    }

    public static void initViewModel(RefAware viewModelObject) {
        Ref ref = viewModelObject.getRef();
        if (ref == null) {
            throw new IllegalArgumentException("View model object " + viewModelObject + " not properly initialized - no Ref assigned yet.");
        }
        ViewModelSupport.invokePostProcessorsOn(viewModelObject, ref);
    }
}
