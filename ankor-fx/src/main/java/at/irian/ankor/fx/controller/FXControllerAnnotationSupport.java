package at.irian.ankor.fx.controller;

import at.irian.ankor.annotation.BeanAnnotationChangeEventListener;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Thomas Spiegl
 */
public class FXControllerAnnotationSupport {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationSupportControllerFactory.class);

    private final Map<Object, ChangeEventListener> controllers;

    private FXControllerAnnotationSupport() {
        controllers = new WeakHashMap<>();
    }

    private static final FXControllerAnnotationSupport INSTANCE = new FXControllerAnnotationSupport();

    public static FXControllerAnnotationSupport annotationSupport() {
        return INSTANCE;
    }

    public void registerChangeListener(Object controller, Ref listenToModelRef) {
        controllers.put(controller,
                new BeanAnnotationChangeEventListener(controller, listenToModelRef.path(), SimpleELPathSyntax.getInstance()));
    }

    public void registerChangeListeners(Object controller) {
        controllers.put(controller,
                new BeanAnnotationChangeEventListener(controller, null, SimpleELPathSyntax.getInstance()));
    }

    Collection<ChangeEventListener> getChangeListeners() {
        return controllers.values();
    }

}
