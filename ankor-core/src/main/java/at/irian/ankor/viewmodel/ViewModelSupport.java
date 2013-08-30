package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public final class ViewModelSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelSupport.class);

    private ViewModelSupport() {}

    public static void invokePostProcessorsOn(Object viewModelObject, Ref viewModelRef) {
        for (ViewModelPostProcessor viewModelPostProcessor : viewModelRef.context().viewModelPostProcessors()) {
            viewModelPostProcessor.postProcess(viewModelObject, viewModelRef);
        }
    }
}
