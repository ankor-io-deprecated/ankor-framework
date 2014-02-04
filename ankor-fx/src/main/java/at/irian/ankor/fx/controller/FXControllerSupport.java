package at.irian.ankor.fx.controller;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.listener.ActionListenersPostProcessor;
import at.irian.ankor.viewmodel.listener.ChangeListenersPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

/**
 * @author Thomas Spiegl
 */
public final class FXControllerSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationSupportControllerFactory.class);

    private FXControllerSupport() {}

    public static void init(Object controller, Ref viewModelRef) {
        BeanMetadata md = viewModelRef.context().metadataProvider().getMetadata(controller);
        new ActionListenersPostProcessor().postProcess(controller, viewModelRef, md);
        new ChangeListenersPostProcessor().postProcess(controller, viewModelRef, md);
    }

}
