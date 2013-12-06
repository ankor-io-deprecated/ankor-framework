package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBeanInitializer;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

/**
 * @author Manfred Geiler
 */
public class AnnotationViewModelPostProcessor implements ViewModelPostProcessor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationViewModelPostProcessor.class);

    private final ViewModelBeanInitializer viewModelBeanInitializer = new ViewModelBeanInitializer();

    @Override
    public void postProcess(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata) {
        viewModelBeanInitializer.init(viewModelObject, viewModelRef, metadata);
    }

}
