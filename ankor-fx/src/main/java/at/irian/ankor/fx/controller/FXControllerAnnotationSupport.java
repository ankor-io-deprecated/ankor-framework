package at.irian.ankor.fx.controller;

import at.irian.ankor.annotation.AnnotationViewModelBeanIntrospector;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBeanInitializer;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

/**
 * @author Thomas Spiegl
 */
public final class FXControllerAnnotationSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationSupportControllerFactory.class);

    private static final AnnotationViewModelBeanIntrospector ANNOTATION_VIEW_MODEL_BEAN_INTROSPECTOR
            = new AnnotationViewModelBeanIntrospector();

    private static final ViewModelBeanInitializer VIEW_MODEL_BEAN_INITIALIZER
            = new ViewModelBeanInitializer();

    private FXControllerAnnotationSupport() {}

    public static void scan(Ref controllerRef, Object controller) {
        BeanMetadata info = ANNOTATION_VIEW_MODEL_BEAN_INTROSPECTOR.getMetadata(controller);
        VIEW_MODEL_BEAN_INITIALIZER.init(controller, controllerRef, info);
    }

}
