package at.irian.ankor.state;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;

/**
 * @author Manfred Geiler
 */
public class StateHolderViewModelPostProcessor implements ViewModelPostProcessor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StateHolderViewModelPostProcessor.class);

    @Override
    public void postProcess(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata) {
        for (PropertyMetadata propertyMetadata : metadata.getPropertiesMetadata()) {
            if (propertyMetadata.isStateHolder()) {
                StateHolderDefinition stateHolderDefinition
                        = viewModelRef.context().modelSession().getStateHolderDefinition();
                String propertyName = propertyMetadata.getPropertyName();
                Ref propertyRef = viewModelRef.appendPath(propertyName);
                stateHolderDefinition.add(propertyRef.path(), propertyMetadata.getPropertyType());
            }
        }
    }

}
