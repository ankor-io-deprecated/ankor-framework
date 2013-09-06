package at.irian.ankor.fx.controller;

import at.irian.ankor.annotation.BeanAnnotationScanner;
import at.irian.ankor.annotation.BeanLifeline;
import at.irian.ankor.ref.Ref;

/**
 * @author Thomas Spiegl
 */
public final class FXControllerAnnotationSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationSupportControllerFactory.class);

    private FXControllerAnnotationSupport() {}

    public static void scan(Ref controllerRef, Object controller) {
        new BeanAnnotationScanner().scan(controller,
                                         controllerRef,
                                         new BeanLifeline(controller));
    }

}
