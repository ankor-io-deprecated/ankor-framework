package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;

/**
 * @author Manfred Geiler
 */
public class AnnotationViewModelPostProcessor implements ViewModelPostProcessor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationViewModelPostProcessor.class);

    @Override
    public void postProcess(Object viewModelObject, Ref viewModelRef) {
        new BeanAnnotationScanner().scan(viewModelObject, viewModelRef, new RefLifeline(viewModelRef));
    }

}
